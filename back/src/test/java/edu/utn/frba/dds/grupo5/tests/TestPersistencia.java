package edu.utn.frba.dds.grupo5.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.utn.frba.dds.grupo5.entidades.Cuenta;
import edu.utn.frba.dds.grupo5.entidades.Empresa;
import edu.utn.frba.dds.grupo5.entidades.Indicador;
import edu.utn.frba.dds.grupo5.entidades.Periodo;
import edu.utn.frba.dds.grupo5.indicadores.IndicadorException;
import edu.utn.frba.dds.grupo5.service.ServiceManager;

public class TestPersistencia {
	//	TODO
	
	private static List<Empresa> empresas;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() throws IndicadorException, Exception {
		String empresasString = IOUtils.toString(TestIndicadores.class.getClassLoader().getResource("empresas.json"));
		Type listType = new TypeToken<ArrayList<Empresa>>(){}.getType();
		empresas = ((List<Empresa>)new Gson().fromJson(empresasString, listType));
		
		String cuentasString = IOUtils.toString(TestIndicadores.class.getClassLoader().getResource("cuentas.json"));
		listType = new TypeToken<ArrayList<Cuenta>>(){}.getType();
		List<Cuenta> cuentas = ((List<Cuenta>)new Gson().fromJson(cuentasString, listType));
		
		ServiceManager.getInstance().guardarCuentas(cuentas);
		
		String indicadoresString = IOUtils.toString(TestIndicadores.class.getClassLoader().getResource("indicadores-predefinidos.json"));
		listType = new TypeToken<ArrayList<Indicador>>(){}.getType();
		List<Indicador> indicadores = ((List<Indicador>)new Gson().fromJson(indicadoresString, listType));
		
		ServiceManager.getInstance().guardarIndicadores(indicadores);
		
		String indicadoresUsuario = IOUtils.toString(TestIndicadores.class.getClassLoader().getResource("indicadores-usuario.json"));
		listType = new TypeToken<ArrayList<Indicador>>(){}.getType();
		List<Indicador> indicadoresUsuarios = new Gson().fromJson(indicadoresUsuario, listType);
		ServiceManager.getInstance().guardarIndicadores(indicadoresUsuarios);
	}
	
	@Test
	public void testEmpresa() throws Exception{
		for(Empresa emp: empresas){
			ServiceManager.getInstance().guardarEmpresa(emp);
		}
		
		Empresa snapchat = ServiceManager.getInstance().buscarEmpresa("Snapchat");
		
		assertEquals("Snapchat",snapchat.getNombre());
		assertEquals(Integer.valueOf(2011),snapchat.getAnioFundacion());
		assertEquals(2,snapchat.getPeriodos().size());
	} 

	
	@Test
	public void testPeriodos() throws Exception{
		Empresa facebook = ServiceManager.getInstance().buscarEmpresa("Facebook");
		Periodo semestral = facebook.getPeriodoByName("Semestral");
		
		assertEquals("Facebook",facebook.getNombre());
		assertEquals(1,facebook.getPeriodos().size());
		assertEquals(3,semestral.getCuentas().size());
		assertEquals("Semestral",semestral.getNombre());
		assertEquals("2017",semestral.getAnio());
	}
	
	@Test
	public void testMetodologias() throws Exception, IndicadorException{

		empresas = ServiceManager.getInstance().evaluateMetodologia("Buffet", empresas);
		
		 assertTrue(empresas.size()==3);
		 assertEquals("Pepsico",empresas.get(0).getNombre());
		 assertEquals("Apple",empresas.get(1).getNombre());
		 assertEquals("Google",empresas.get(2).getNombre());
		
	}
	@Test
	public void testIndicadores() throws Exception{	
		Indicador indicador1 = ServiceManager.getInstance().recuperarIndicador("indicador1");
		Empresa snapchat = ServiceManager.getInstance().buscarEmpresa("Snapchat");
		
		assertEquals("indicador1", indicador1.getNombre());
		assertEquals(2, indicador1.getCuentas().size());
		assertEquals("cuenta{EBITDA}*2+cuenta{CASH}", indicador1.getExpression());
		assertEquals(250400, ServiceManager.getInstance().evaluarIndicador("indicador1", snapchat, "Semestral"),0);
		
	}
@AfterClass
	public static void clearDatabase() throws Exception{
		ServiceManager.getInstance().clearRepo();
	}	
	
}