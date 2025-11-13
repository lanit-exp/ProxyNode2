package lanit_exp.proxy_node.helpers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionHelper {


    public static Set<String> getDuplicatesInList(List<String> list){
        return list.stream()
                .filter(string -> Collections.frequency(list, string) > 1)
                .collect(Collectors.toSet());
    }


}
