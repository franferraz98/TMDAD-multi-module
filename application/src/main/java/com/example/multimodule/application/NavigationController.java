package com.example.multimodule.application;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class NavigationController {

    public class GroupSimulator {
        public String name;
        public String url;

        public GroupSimulator(String name, String url) {
            this.name = name;
            this.url = url;
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

        System.out.println("USERNAME: " + username);

        model.addAttribute("activePage", "groups");
        GroupSimulator g1 = new GroupSimulator("Grupo1","http://localhost:8080/group/1");
        GroupSimulator g2 = new GroupSimulator("Grupo2","http://localhost:8080/group/2");
        GroupSimulator g3 = new GroupSimulator("Grupo3","http://localhost:8080/group/3");
        List<GroupSimulator> l = new ArrayList<GroupSimulator>();
        l.add(g1);
        l.add(g2);
        l.add(g3);
        model.addAttribute("groups", l);
        return "info";
    }

    @GetMapping("/group/{id}")
    public String getContact(Model model, @PathVariable String id, @RequestParam(value = "from", required = false) String from){
        model.addAttribute("activePage", "group" + id);
        System.out.println("FROM: " + from);
        model.addAttribute("from", from);
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
