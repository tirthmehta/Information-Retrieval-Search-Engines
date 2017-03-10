import java.util.ArrayList;

public class CrawlStat {
static ArrayList<String[]> a1=new ArrayList<>();
static ArrayList<String[]> a2=new ArrayList<>();
ArrayList<String[]> get()
{
	return a1;
}
public void add(String[] entries) {
	// TODO Auto-generated method stub
	a1.add(entries);
}

ArrayList<String[]> get2()
{
	return a2;
}
public void add2(String[] entries) {
	// TODO Auto-generated method stub
	a2.add(entries);
}
}
