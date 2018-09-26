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
		this.list = new ArrayList<>();
		this.level = lvl;
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
}
