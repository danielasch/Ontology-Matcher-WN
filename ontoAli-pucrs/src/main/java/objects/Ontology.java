package objects;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;


public class Ontology {
//Atributes
	
	//Ontology file name
	private String fileName;
	//Ontology ID
	protected OWLOntologyID ontologyID;
	//OWLOntology loads the ontology file
	protected OWLOntology ontology;
	//Used to manipulate de ontology
	protected OWLOntologyManager manager;
		
//Constructor
		
	/*
	* creates a new Ontology class and extract the necessary information 
	* about the ontology with shouldLoad method. 
	*/
	public Ontology(String _file) {
		try {
			shouldLoad(_file);
		} catch(OWLOntologyCreationException e) {
			System.out.println("Failed to load ontology: " + _file);
			System.out.println("erro: " + e);
		} catch(FileNotFoundException e) {
			System.out.println("File not found: " + _file);
			System.out.println("erro: " + e);
		} catch(IOException e) {
			System.out.println("I/O operation failed: " + _file);
			System.out.println("erro: " + e);
		}

	}
	
//Getters and setters
	
	private void set_fileName(String _file) {
		int i = _file.lastIndexOf("/");
		String fname;
		fname = _file.substring(i);
		fileName = fname;
	}
	
	public String get_fileName() {
		return fileName;
	}
	
	protected void set_ontologyID(OWLOntologyID ontoID) {
		ontologyID = ontoID;
	}
	
	public OWLOntologyID get_ontologyID() {
		return ontologyID;
	}
	
	protected void set_ontology(OWLOntology onto) {
		ontology = onto;
	}
	
	public OWLOntology get_ontology() {
		return ontology;
	}
	
	protected void set_ontologyManager(OWLOntologyManager _manager) {
		manager = _manager;
	}
	
	protected OWLOntologyManager get_ontologyManager() {
		return manager;
	}
//Methods
	
	/*
	 * Loads the ontology into OWLOntology and extract the Ontology ID, IRI and manager.
	 */	
	protected void shouldLoad(String _file) throws OWLOntologyCreationException, FileNotFoundException, IOException {
		
		File file = new File(_file);
		
		set_fileName(_file);
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology local = manager.loadOntologyFromOntologyDocument(file);
		IRI documentoIRI = manager.getOntologyDocumentIRI(local);
		
		set_ontologyManager(manager);
		set_ontology(local);
		
		System.out.println("Loaded Ontology: " + local);
		System.out.println("From: " + documentoIRI + "\n");
		
		OWLOntologyID ID = local.getOntologyID();
		set_ontologyID(ID);
		
		System.out.println(ID);
		System.out.println(ID.getOntologyIRI() + "\n");		
	}
	
}
