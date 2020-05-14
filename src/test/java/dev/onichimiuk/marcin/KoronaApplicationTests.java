package dev.onichimiuk.marcin;

import dev.onichimiuk.marcin.geolocation.GeoLocation;
import dev.onichimiuk.marcin.warehouse.WarehouseService;
import dev.onichimiuk.marcin.warehouse.model.Warehouse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RunWith(SpringRunner.class)
@SpringBootTest
class KoronaApplicationTests {

	private Integer x = null;
	private Integer y = null;
	private GeoLocation location = new GeoLocation() { @Override public long getX() { return x; } @Override public long getY() { return y; } };
	private Map<String, Integer> map = new TreeMap<>();

	@Autowired
	private WarehouseService service;

	@Test
	public void test_findNearestConfiguration_singleProductMap_returnsNearestSingleWarehouse() throws Exception {
		//given
		map.clear();
		map.put("pasta",15);
		x = 1928; y = 5147; // Łódź

		//when
		var result = service.testMethod(location, map);

		//then
		List<Warehouse> resultList = new ArrayList<>(result);
		assertEquals("Kraków", resultList.get(0).getCity());
	}

	@Test
	public void test_findNearestConfiguration_fewProductsMap_returnsNearestWarehouses() throws Exception {
		//given
		map.clear();
		map.put("rice",20); map.put("pasta",50); map.put("water",25);
		x = 1530; y = 5156; // Zielona Góra

		//when
		var result = service.testMethod(location,map);

		//then
		assertTrue(result.removeIf(w -> w.getCity().equals("Zielona Góra")));
		assertTrue(result.removeIf(w -> w.getCity().equals("Łódź")));
		assertTrue(result.removeIf(w -> w.getCity().equals("Poznań")));
		assertEquals(0, result.size());
	}

	@Test
	public void test_findNearestConfiguration_emptyProductMap_returnsEmptyList() throws Exception {
		//given
		map.clear();
		x = 1928; y = 5147; // Łódź

		//when
		var result = service.testMethod(location,map);

		//then
		assertEquals(0, result.size());
	}

	@Test
	public void test_findNearestConfiguration_tooMuchAmountProduct_returnsBusinessError() throws Exception {
		//given
		map.clear();
		map.put("pasta",9999);
		x = 1928; y = 5147; // Łódź

		//when then
		try{
			var result = service.testMethod(location,map);
		} catch (NullPointerException e){
			assertEquals("Produkt pasta nie występuje w ilości 9999 w żadnym magazynie. Zmodyfikuj zamówienie.", e.getMessage());
		}
	}
}
