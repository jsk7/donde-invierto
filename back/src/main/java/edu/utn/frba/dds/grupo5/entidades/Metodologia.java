package edu.utn.frba.dds.grupo5.entidades;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.utn.frba.dds.grupo5.util.Util;


@Table(name="di_metodologia")
@Entity
@NamedQueries({
	@NamedQuery(name="search_all_metodologias",query="select m from Metodologia m "),
	@NamedQuery(name="search_metodologias",query="select m from Metodologia m where m.nombre = :nombre")
})
public class Metodologia extends Persistent{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 2665655157388244421L;

	public Metodologia(){
    	
    }
	
    private String nombre;
	private List<Empresa> empresas;
    private List<ICondicion> condiciones;
    private List<IFiltro> filtros;
	
	public void minimizar(String indicador,int anios) throws Exception{
		ICondicion cond = new CondicionMinimo();
		cond.setIndicador(indicador);
		cond.setAnios(anios);
		getCondiciones().add(cond);
	}
	
	public void maximizar(String indicador,int anios) throws Exception{
		ICondicion cond = new CondicionMaximo();
		cond.setIndicador(indicador);
		cond.setAnios(anios);
		getCondiciones().add(cond);
	}
	
	public void consistencia(String indicador, int anios) throws Exception{
		IFiltro filtro = new FiltroConsistencia();
		filtro.setIndicador(indicador);
		filtro.setAnios(anios);
		getFiltros().add(filtro);
	}
	
	public void aniosAntiguedad(Integer anios){
		IFiltro filtro = new FiltroAntiguedad();
		filtro.setAnios(anios);
		getFiltros().add(filtro);
	}
	
	public void ordenarConsistencia(String indicador, int anios){
		ICondicion cond = new CondicionConsistencia();
		cond.setIndicador(indicador);
		cond.setAnios(anios);
		getCondiciones().add(cond);
	}
	
	public void ordenarAntiguedad(){
		ICondicion cond = new CondicionAntiguedad();
		getCondiciones().add(cond);
	}
	
	@Transient
	public List<Empresa> getResult(){
		
		List<Empresa> result = Util.filterByPredicate(getEmpresas(), e -> filtrar(e));
		Collections.sort(result, new Comparator<Empresa>() {
			public int compare(Empresa o1, Empresa o2) {
				
				Integer mayorE1 = Util.filterByPredicate(condiciones, c -> {return c.evaluate(o1, o2)>0;}).size();
				Integer mayorE2 = Util.filterByPredicate(condiciones, c -> {return c.evaluate(o2, o1)>0;}).size();
				
				return mayorE2.compareTo(mayorE1);
			}
		});
		return result;
	}

	private boolean filtrar(Empresa e) {
		for(IFiltro filtro: filtros){
			if(!filtro.evaluate(e))
				return false;
		}
		return true;
			
	}
	
	@Transient
	public List<ICondicion> getCondiciones() {
		if(condiciones==null)
			condiciones = new ArrayList<>();
		return condiciones;
	}

	public void setCondiciones(List<ICondicion> condiciones) {
		this.condiciones = condiciones;
	}

	@Column(name="met_nombre",length=200,nullable=false)
	public String getNombre(){
		return nombre;
	}
	
	public void setNombre(String nombre){
		this.nombre=nombre;
	}
	
	@Transient
	public List<Empresa> getEmpresas() {
		if(empresas == null){
			empresas = new ArrayList<>();
		}
		return empresas;
	}
	
	public void setEmpresas(List<Empresa> empresas) {
		this.empresas = empresas;
	}
	
	@Transient
	public List<IFiltro> getFiltros() {
		if(filtros==null)
			filtros=new ArrayList<>();
		return filtros;
	}

	public void setFiltros(List<IFiltro> filtros) {
		this.filtros = filtros;
	}

}
