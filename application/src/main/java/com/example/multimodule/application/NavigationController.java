package com.example.multimodule.application;

import com.example.multimodule.fileupload.FileController;
import com.example.multimodule.fileupload.FileDBGrupo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NavigationController {

    @Autowired
    private static FileController fileController;

    public class GroupSimulator {
        public String name;
        public String url;

        public GroupSimulator(String name, String url) {
            this.name = name;
            this.url = url;
        }

    }

    public static class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> params)
                throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
        }
    }

    @GetMapping("/")
    public String getIndex(Model model, @RequestParam(value = "from", required = false) String from,
                           @RequestParam(value = "username", required = false) String username){
        model.addAttribute("activePage", "index");
        model.addAttribute("from", from);
        model.addAttribute("username", username);
        return "index";
    }

    @GetMapping("/info")
    public String getInfo(Model model, @RequestParam(value = "from", required = false) String from,
    @RequestParam(value = "username", required = false) String username){

        model.addAttribute("activePage", "groups");

        /*
        try {
            URL url = new URL("http://localhost:8080/getGroups");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("username", username);

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            String response =  con.getResponseMessage();
            System.out.println(response);
        } catch (Exception e){
            System.err.println("Failed to access DB... " + e);
        }

        List<FileDBGrupo> lista = fileController.getGroups(username);
        List<GroupSimulator> l = new ArrayList<GroupSimulator>();
        for(int i = 0; i<lista.size(); i++){
            String name = lista.get(i).getName();
            String url = "http://localhost:8080/group/" + name;
            l.add( new GroupSimulator(name, url) );
        }

        model.addAttribute("groups", l);

         */
        List<GroupSimulator> l = new ArrayList<GroupSimulator>();
        model.addAttribute("groups", l);
        return "info";
    }

    @GetMapping("/group/{id}")
    public String getContact(Model model, @PathVariable String id){
        model.addAttribute("activePage", "group/" + id);
        return "group";
    }

    @GetMapping("/database")
    public String getDatabase(Model model) {
        model.addAttribute("activePage", "database");
        return "databaseFile";
    }

    @GetMapping("/signup")
    public String getSignup(Model model) {
        model.addAttribute("activePage", "signup");
        return "signup";
    }

    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("activePage", "login");
        return "login";
    }

    @GetMapping("/admin")
    public String getAdmin(Model model) {
        model.addAttribute("activePage", "admin");
        return "admin";
    }

}
