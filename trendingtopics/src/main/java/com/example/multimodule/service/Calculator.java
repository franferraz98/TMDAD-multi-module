package com.example.multimodule.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.*;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class Calculator {

    private HashMap<String,Integer> topics;
    private ConcurrentMap<String, List<Long>> times;
    private Long threshold;

    @Autowired
    private TTRunner ttRunner;

    public Calculator(){
        this.topics = new HashMap<>();
        this.times = new ConcurrentHashMap<>();
        this.threshold = 60000L;
    }


    public String removeBuzzwords(String message){
        // Quitar las palabras poco interesantes
        message = message.replace(",","");
        message = message.replace(".","");
        message = message.replace(";","");
        message = message.replace("{","");
        message = message.replace("}","");
        message = message.replace("¡","");
        message = message.replace("!","");
        message = message.replace("¿","");
        message = message.replace("?","");
        return message;
    }

    public void parseMessage(String message){
        if(!message.equals("")){
            String text[] = message.split(":::");
            message = text[1];

            message = removeBuzzwords(message);
            String[] parts = message.split(" ");
            for(int i = 0 ; i< parts.length; i++){
                if(this.topics.containsKey(parts[i])){
                    this.topics.put(parts[i], this.topics.get(parts[i]) + 1);
                } else {
                    this.topics.put(parts[i], 1);
                }
                List<Long> lista = this.times.get(parts[i]);
                if(lista == null){
                    lista = new ArrayList<>();
                }
                lista.add(System.currentTimeMillis());
                this.times.put(parts[i], lista);
            }
            //System.out.println(times.toString());
        }
    }


    // https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
    public LinkedHashMap<String, Integer> sortHashMapByValues(
            HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        Collections.reverse(mapKeys);
        Collections.reverse(mapValues);

        LinkedHashMap<String, Integer> sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    @Scheduled(fixedRate = 5000L)
    public void reset(){
        Set<String> keys = this.times.keySet();
        Iterator<String> iterator = keys.iterator();
        Long current = System.currentTimeMillis();
        while (iterator.hasNext()) {
            String key = iterator.next();
            List<Long> values = this.times.get(key);
            Iterator<Long> iter = values.iterator();
            while (iter.hasNext()){
                Long val = iter.next();
                if(current-val > this.threshold){
                    List<Long> aux = new ArrayList<>(values.size());
                    Iterator<Long> auxIter = values.iterator();
                    while (auxIter.hasNext()){
                        Long iterVal = auxIter.next();
                        aux.add(iterVal);
                    }
                    aux.remove(val);
                    if(aux.isEmpty()){
                        this.times.remove(key);
                        this.topics.remove(key);
                    } else {
                        this.times.put(key, aux);
                        this.topics.put(key, this.topics.get(key) - 1);
                    }
                }
            }
        }
        System.out.println(this.times);
    }

    @Scheduled(fixedRate = 5000L)
    public void sendMessage(){
        LinkedHashMap<String, Integer> lH = sortHashMapByValues(this.topics);
        Gson gson = new Gson();
        String json = gson.toJson(lH);
        try{
            //this.ttRunner.run(json);
            //int randomNum = ThreadLocalRandom.current().nextInt(0, 999 + 1);
            this.ttRunner.run("admin:::" + json + "+++trendingtopics");
            //System.out.println(json);
        } catch (Exception e){
            System.err.println("Error in send");
        }
    }


}
