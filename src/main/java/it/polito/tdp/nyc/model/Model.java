package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	
	private NYCDao dao;
	private Graph<String, DefaultWeightedEdge> grafo;
	
	
	public Model() {
		dao = new NYCDao();
	}
	
	public void creaGrafo(String borough) {
		
		this.grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		List<String> VerticiBorghi = dao.getVertici(borough);
		Graphs.addAllVertices(this.grafo, VerticiBorghi);
		
		List<Arco> SSIDBorghi = dao.getArchi(borough);
		Arco temp = new Arco(null, null);
		for(Arco a1 : SSIDBorghi) {
			for(Arco a2 : SSIDBorghi) {
				
				if(!a1.equals(a2)) {
					List<String> unione = new ArrayList<>(a1.getSSID());
					unione.addAll(a2.getSSID());
					Graphs.addEdge(this.grafo, a1.getNTACode(), a2.getNTACode(), unione.size());	
				
				}
			}
		}
		
		System.out.println("I vertici sono: "+ this.grafo.vertexSet().size()+ ", gli Archi: "+ this.grafo.edgeSet().size());
		
	}
	
	
	public List<String> getBorough(){
		return dao.getBorough();
	}
	
	public List<NTA> AnalisiArchi(){
		
		double media = 0.0;
		
		//calcolo la media dei pesi
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			media = media + this.grafo.getEdgeWeight(e);
		}
		 
		media = media/this.grafo.edgeSet().size();
		
		List<NTA> result = new ArrayList<>();
		
		//creo una lista di oggetti con gli archi maggiori della media
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>media) {
				
				result.add(new NTA(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e),
						 this.grafo.getEdgeWeight(e)));
			}
		}
		
		Collections.sort(result, new Comparator<NTA>() {
			@Override
			public int compare(NTA o1, NTA o2) {
				return (int) (o2.getPeso() - o1.getPeso());
			}
		});
		
		
		
		return result;
		
	}
}

