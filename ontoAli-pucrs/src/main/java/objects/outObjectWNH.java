package objects;

import java.util.ArrayList;
import java.util.List;

import edu.mit.jwi.item.ISynset;

public class outObjectWNH {
	private ISynset obj;
	private List<outObjectWNH> list;
	private int level;
	
	public outObjectWNH(ISynset synset, int lvl) {
		this.obj = synset;
		//this.list = null;
		this.list = new ArrayList<>();
		this.level = lvl;
	}
	
	public outObjectWNH() {
		this.obj = null;
		//this.list = null;
		this.list = null;
		this.level = 0;
	}
	
	public void print() {
		System.out.println(this.level + "  " + this.obj.toString() + "\n");
	}
	
	public int get_level() {
		return this.level;
	}
	
	public String get_print() {
		return this.obj.toString();
	}
	
	public void add_list(outObjectWNH nc) {
		this.list.add(nc);
	}
	
	public List<outObjectWNH> get_list() {
		return this.list;
	}
	
	public void set_synset(ISynset syn) {
		this.obj = syn;
	}
	
	public void set_cont(int cont) {
		this.level = cont;
	}
	
	public void create_list() {
		this.list = new ArrayList<>();
	}
}
