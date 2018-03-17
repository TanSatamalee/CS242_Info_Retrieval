import java.nio.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class HadoopSearch {

    public static void main(String[] args) throws Exception {
    	String[] query_args = args;
        int num_results = 3;
        
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        List<String> q = Arrays.asList(query_args);
        
        File file = new File("hadoop2.txt");
        Scanner sc = new Scanner(file);

        String[] split;
        String[] ms;
        int j;
        while (sc.hasNextLine()) {
            split = sc.nextLine().replaceAll("\t", " ").split(" ");
            if (q.contains(split[0])) {
                for (j = 1; j < split.length; j++) {
                    ms = split[j].split(":");
                    if (map.containsKey(ms[0])) {
                        map.replace(ms[0], map.get(ms[0]) + Integer.parseInt(ms[1]));
                    } else {
                        map.put(ms[0], Integer.parseInt(ms[1]));
                    }
                }
            }
        }

        List<String> keys = new ArrayList<String>(map.keySet());
        List<Integer> values = new ArrayList<Integer>(map.values());
        Collections.sort(keys, Collections.reverseOrder());
        Collections.sort(values, Collections.reverseOrder());

        LinkedHashMap<String,Integer> sorted = new LinkedHashMap<String,Integer>();

        Iterator valueIter = values.iterator();
        while (valueIter.hasNext()) {
            Integer val = (Integer) valueIter.next();
            Iterator keyIter = keys.iterator();

            while (keyIter.hasNext()) {
                String key = (String)keyIter.next();
                int c1 = (Integer) map.get(key);
                int c2 = val;

                if (c1 == c2){
                    map.remove(key);
                    keys.remove(key);
                    sorted.put(key, val);
                    break;
                }

            }
        }
        
        Iterator solution = sorted.entrySet().iterator();

        while (num_results > 0 && solution.hasNext()) {
            String bestMovie = solution.next().toString().split("=")[0];
            System.out.println(bestMovie + ".txt");
            num_results -= 1;

            /* CODE GOES HERE */
        }
    }

}
