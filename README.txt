
        Ontology-Matcher                               					                											                        
	This product includes software developed at PUCRS  	 
	by the NLP team 									                
	(http://www.inf.pucrs.br/linatural/wordpress/).		   
------------------------------------------------------

        About Ontology-Matcher						                  
														                         
	Ontology-Matcher is an ontology matching system that 		  
	generates alignments between top ontologies 		    
	(DOLCE, SUMO and DUL) and any domain ontology. 		 
   													                       
	If you use Ontology-Matcher, please cite the following		 
	publication:										                   
														                         
	For more information, read:							         
		- D. Schmidt, R. Basso, C. Trojahn, R. Vieira.	  
		"Matching domain and top-level ontologies via	   
		OntoWordNet", International Workshop on OM,		   
		2017. (english)									                 
		- R. Basso, D. Schmidt, C. Trojahn, R. Vieira.	  
		"Top-level and Domain ontology Alignment via 	    
		WordNet", IX Seminar on Ontology Research in 	   
		Brazil, 2017. (BR-portuguese)				          
------------------------------------------------------

        System Requirements					               
														                        
	Ontology-Matcher requires Java SE Runtime Environmet 8+.	 
														                        
	CPU: Intel Core i5-2400 @ 3.10GHz or better			   
	Memory: 4GB RAM or higher							              
         Keep in mind that memory usage depends on the 	  
         size of the ontologies. Therefore, opening 		    
         large ontologies may take a while. 			      
------------------------------------------------------
       
        Ontology-Matcher Eclipse IDE Usage			          
														                       
	Go to:                                           
	                                                    
        File-> Import...                         
                          								           
	Select:                                            
														                         
	Maven-> Existing Maven Projects                				      
							                                       
	Then select the root directory as the location of ontoAli-pucrs folder
	
	OBS: To use the word embeddings technique, you     
	must download the GloVe model:                      
	http://nlp.stanford.edu/data/glove.6B.zip          
	Copy the glove.6B.200d.txt into the resources folder!! 
                                                     
        Running:                                          
	                                                    
	Run Configuration-> Java Aplication
	
        Select main class as Main.java
	
	The arguments order are:							             
														                      
	-[domain ontology path]								             
		ex: C:/Users/.../ontology.owl					            
	-[rdf alignment path] (to be generated)				        
		ex: C:/Users/.../alignment.rdf					          
	-[top ontology] (dolce, sumo or dul)				        
		ex: dolce										                      
	-[alignment technique] (0, 1, 2 or 3)				        
		ex: 2											                        
	-[reference alignment path] OPTIONAL				       
		ex: C:/Users/.../referenceAlign.rdf				       
														                        
	Examples:											                    
														                      
	a) Use Ontology-Matcher to align a domain ontology with 	 
	dolce, sumo or dul:									               
	C:/Users/.../ontology.owl 
	C:/Users/.../alignment.rdf 
	sumo 
	2					         
														                         
	b) Use Ontology-Matcher to align a domain ontology with	  
	dolce, sumo or dul and evaluate the genereted		      
	alignment with a reference alignment:				        
	C:/Users/.../ontology.owl 
	C:/Users/.../alignment.rdf 
	sumo 
	2 					        
	C:/Users/.../refAlign.rdf	                          	                                                                 
------------------------------------------------------

        About Techniques					                
														                         
	These techniques modify the synset disambiguation	  
	method.												                     
														                          
	1 -> Based on lesk word sense disambiguation:		   
		Overlaps the context with the bag of words of	    
		a synset. 										                    
	2 -> Word Embedding disambiguation:					       
		Uses the average cossine distance between the	    
		context and the bag of words of a synset.		      
	3 -> Developing										                  

