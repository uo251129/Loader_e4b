package parsertest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import parser.*;
import persistence.UserFinder;
import persistence.util.Jpa;

import static org.junit.Assert.*;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.junit.After;
import org.junit.Test;

import com.lowagie.text.DocumentException;
import model.User;

public class ParserTest {

	@Test
	public void testLoadExcelExito() throws FileNotFoundException, DocumentException {
		RList ex = new RList();
		ex.load("src/test/resources/test.xlsx");

		assertEquals(ex.getAllUsers().size(), 3);

		List<XSSFCell> list1 = ex.getAllUsers().get(0);
		List<XSSFCell> list2 = ex.getAllUsers().get(1);
		List<XSSFCell> list3 = ex.getAllUsers().get(2);

		StringBuilder st = new StringBuilder();

		for (int i = 0; i <= 3; i++) {
			if (i != 3)
				st.append(list1.get(i).toString() + " / ");
			else
				st.append(list1.get(i).toString());
		}

		assertEquals(st.toString(), "Juan Torres Pardo / 22.971436; -43.182565 / juan@example.com / 87654321P");

		st = new StringBuilder();

		for (int i = 0; i <= 3; i++) {
			if (i != 3)
				st.append(list2.get(i).toString() + " / ");
			else
				st.append(list2.get(i).toString());
		}

		assertEquals(st.toString(), "Luis López Fernando / 32.97; -23.1 / luis@example.com / 19160962F");

		st = new StringBuilder();

		for (int i = 0; i <= 3; i++) {
			if (i != 3)
				st.append(list3.get(i).toString() + " / ");
			else
				st.append(list3.get(i).toString());
		}

		assertEquals(st.toString(), "Ana Torres Pardo / 21.26; 50,26 / ana@example.com / 09940449X");
	}
	
	@Test
	public void testLoadCSVExito() throws FileNotFoundException, DocumentException {
		RMaster master = new RMaster();
		master.load("src/test/resources/master.csv");
		
		assertEquals(master.getAllTypes().size(), 3);
		
		String[] test = {
				"Type [code=1, type=Person]",
				"Type [code=2, type=Entity]",
				"Type [code=3, type=Sensor]"};
		
		for(int i=0;i<master.getAllTypes().size();i++) 
			assertEquals(test[i],master.getAllTypes().get(i).toString());	
	}

	@Test(expected = FileNotFoundException.class)
	public void testLoadExcelFicheroNoEncontrado() throws FileNotFoundException, DocumentException {
		RList ex = new RList();
		ex.load("src/test/resources/fallo.xlsx");

		assertEquals(ex.getAllUsers().size(), 3);

		List<XSSFCell> list1 = ex.getAllUsers().get(0);
		List<XSSFCell> list2 = ex.getAllUsers().get(1);
		List<XSSFCell> list3 = ex.getAllUsers().get(2);
		StringBuilder st = new StringBuilder();

		for (int i = 0; i < list1.size(); i++) {
			st.append(list1.get(i).toString() + " ");
		}

		assertEquals(st.toString(), "Juan Torres Pardo juan@example.com " + list1.get(3)
				+ " C/ Federico García Lorca 2 Español 90500084Y ");

		st = new StringBuilder();

		for (int i = 0; i < list2.size(); i++) {
			st.append(list2.get(i).toString() + " ");
		}

		assertEquals(st.toString(),
				"Luis López Fernando luis@example.com " + list2.get(3) + " C/ Real Oviedo 2 Español 19160962F ");

		st = new StringBuilder();

		for (int i = 0; i < list3.size(); i++) {
			st.append(list3.get(i).toString() + " ");
		}

		assertEquals(st.toString(),
				"Ana Torres Pardo ana@example.com " + list3.get(3) + " Av. De la Constitución 8 Español 09940449X ");
	}
	
	@Test (expected = FileNotFoundException.class)
	public void testLoadCSVNoEncontrado() throws FileNotFoundException, DocumentException {
		RMaster master = new RMaster();
		master.load("src/test/resources/fallo.xlsx");
		
		assertEquals(master.getAllTypes().size(), 0);
	}

	@Test(expected = IOException.class)
	public void testLoadExcelErrorExcel() throws IOException, DocumentException {
		RList ex = new RList();
		ex.load("src/test/resources/vacio.xlsx");

		assertEquals(ex.getAllUsers().size(), 3);

		List<XSSFCell> list1 = ex.getAllUsers().get(0);
		List<XSSFCell> list2 = ex.getAllUsers().get(1);
		List<XSSFCell> list3 = ex.getAllUsers().get(2);
		StringBuilder st = new StringBuilder();

		for (int i = 0; i < list1.size(); i++) {
			st.append(list1.get(i).toString() + " ");
		}

		assertEquals(st.toString(),
				"Juan Torres Pardo juan@example.com 10-oct-1985 C/ Federico García Lorca 2 Español 90500084Y ");

		st = new StringBuilder();

		for (int i = 0; i < list2.size(); i++) {
			st.append(list2.get(i).toString() + " ");
		}

		assertEquals(st.toString(),
				"Luis López Fernando luis@example.com 02-mar-1970 C/ Real Oviedo 2 Español 19160962F ");

		st = new StringBuilder();

		for (int i = 0; i < list3.size(); i++) {
			st.append(list3.get(i).toString() + " ");
		}

		assertEquals(st.toString(),
				"Ana Torres Pardo ana@example.com 01-ene-1960 Av. De la Constitución 8 Español 09940449X ");
	}

	@Test
	public void testReaderSingleton() throws DocumentException {
		ReaderSingleton rS = ReaderSingleton.getInstance();
		rS.loadFile("cadenaIncorrecta");
		rS.loadFile("test.xlsx");
		ReaderSingleton rS1 = ReaderSingleton.getInstance();
		rS1.loadFile("cadenaIncorrecta");
		rS1.loadFile("test.xlsx");
		assertEquals(rS, rS1);
	}
	
	@Test
	public void testReaderSingletonMaster() {
		ReaderSingleton rS = ReaderSingleton.getInstance();
		rS.loadMasterFile("cadenaIncorrecta");
		rS.loadMasterFile("master.csv");
		ReaderSingleton rS1 = ReaderSingleton.getInstance();
		rS1.loadMasterFile("cadenaIncorrecta");
		rS1.loadMasterFile("master.csv");
		assertEquals(rS, rS1);
	}

	@After
	public void deleting() {
		EntityManager mapper = Jpa.createEntityManager();
		EntityTransaction trx = mapper.getTransaction();
		trx.begin();
		List<User> aBorrar = UserFinder.findByIdent("09940449X");
		if (!aBorrar.isEmpty())
			Jpa.getManager().remove(aBorrar.get(0));

		aBorrar = UserFinder.findByIdent("19160962F");
		if (!aBorrar.isEmpty())
			Jpa.getManager().remove(aBorrar.get(0));

		aBorrar = UserFinder.findByIdent("90500084Y");
		if (!aBorrar.isEmpty())
			Jpa.getManager().remove(aBorrar.get(0));

		trx.commit();
		mapper.close();

	}
}
