package Example.Ajax.controllers;

import Example.Ajax.entities.AppUser;
import Example.Ajax.errors.ServiceError;
import Example.Ajax.repositories.UserRepository;
import Example.Ajax.services.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    //ADMIN METHODS:
    //Requires permission granted by "ROLE_ADMIN" before entering the method
    
    //README: To test the admin role validations must create the first admin through MySql DB changing the "ROLE_CLIENT" to "ROLE_ADMIN" of an app user
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/saveAdmin")
    public ResponseEntity<Object> saveAdminPost(@RequestBody AppUser user, ModelMap model) {

        try {
            userService.saveClientUser(user.getName(), user.getDni(), user.getPhone(), user.getMail(), user.getAddress(), user.getPassword(), user.getPassword());
        } catch (ServiceError ex) {
            model.put("error", ex.getMessage());
            model.put("name", user.getName());
            model.put("dni", user.getDni());
            model.put("phone", user.getPhone());
            model.put("mail", user.getMail());

            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    
    //Admin List/
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/adminList")
    public String adminList(Model model) {
        List<AppUser> admins = userRepository.findAll();
        List<AppUser> newList = new ArrayList();
        for (AppUser admin : admins) {
            if (admin.getRol().equals("ROLE_ADMIN")) {
                newList.add(admin);
            }
        }
        model.addAttribute("tittle", "ADMINISTRATOR LIST");
        model.addAttribute("admins", newList);
        return "/adminList";
    }

    //Client List/
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/clientList")
    public String clientList(Model model) {
        List<AppUser> clients = userRepository.findAll();
        List<AppUser> newList = new ArrayList();
        for (AppUser client : clients) {
            if (client.getRol().equals("ROLE_CLIENT")) {
                newList.add(client);
            }
        }
        model.addAttribute("tittle", "CLIENT LIST");
        model.addAttribute("clients", newList);
        return "/clientList";
    }

    //Creates a "client" user, doesn´t require permissions
    @PostMapping("/saveClient")
    public ResponseEntity<Object> saveClientPost(@RequestBody AppUser user, ModelMap model) {

        try {
            userService.saveClientUser(user.getName(), user.getDni(), user.getPhone(), user.getMail(), user.getAddress(), user.getPassword(), user.getPassword());
        } catch (ServiceError ex) {
            model.put("error", ex.getMessage());
            model.put("name", user.getName());
            model.put("dni", user.getDni());
            model.put("phone", user.getPhone());
            model.put("mail", user.getMail());
            model.put("address", user.getAddress());

            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
