
package Example.Ajax.controllers;

import Example.Ajax.entities.AppUser;
import Example.Ajax.repositories.UserRepository;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class MainController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/")
    public String index(Authentication auth, HttpSession session, Model model) {

        if (auth != null) {
            String name = auth.getName();

            if (session.getAttribute("usuario") == null) {
                AppUser usuario = userRepository.findByName(name);

                session.setAttribute("usuario", usuario);
            }
        }

        return "index";
    }

}
