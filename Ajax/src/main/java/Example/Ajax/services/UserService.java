
package Example.Ajax.services;

import Example.Ajax.errors.ServiceError;
import Example.Ajax.entities.AppUser;
import Example.Ajax.repositories.UserRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /*NEW ADMIN*/
    @Transactional
    public void saveAdminUser(String name, String dni, String phone, String mail, String password1, String password2) throws ServiceError {

        validation(name, dni, phone, mail, password1, password2);

        AppUser admin = new AppUser();

        admin.setName(name);
        admin.setDni(dni);
        admin.setPhone(phone);
        admin.setMail(mail);
        admin.setRol("ROLE_ADMIN");
        admin.setAddress(null);
        admin.setActive(Boolean.TRUE);
        admin.setRegistrationDate(new Date());

        /*encriptamos la clave y luego la seteamos*/
        String encryptedPassword = new BCryptPasswordEncoder().encode(password1);
        admin.setPassword(encryptedPassword);

        if (userRepository.findAll().contains(userRepository.findByMail(mail))) {
            throw new ServiceError("Administrator already registered.");
        } else {
            userRepository.save(admin);
        }
    }

    /*NEW CLIENT*/
    @Transactional
    public void saveClientUser(String name, String dni, String phone, String mail, String address, String password1, String password2) throws ServiceError {

        validation(name, dni, phone, mail, password1, password2);

        AppUser client = new AppUser();

        client.setName(name);
        client.setDni(dni);
        client.setPhone(phone);
        client.setMail(mail);
        client.setRol("ROLE_CLIENT");
        client.setAddress(address);
        client.setActive(Boolean.TRUE);
        client.setRegistrationDate(new Date());

        /*encriptamos la clave y luego la seteamos*/
        String encryptedPassword = new BCryptPasswordEncoder().encode(password1);
        client.setPassword(encryptedPassword);

        if (userRepository.findAll().contains(userRepository.findByMail(mail))) {
            throw new ServiceError("Client already registered.");
        } else {
            userRepository.save(client);
        }
        
    }

    /*Edit Admin*/
    @Transactional
    public void editAdmin(String id, String phone, String mail, String password1, String password2) throws ServiceError {

        AppUser admin = userRepository.getById(id);

        validation(admin.getName(), admin.getDni(), phone, mail, password1, password2);

        admin.setPhone(phone);
        admin.setMail(mail);        

        String encryptedPassword = new BCryptPasswordEncoder().encode(password1);
        admin.setPassword(encryptedPassword);

        userRepository.save(admin);

    }
    
    @Transactional
    public void editClient(String id, String phone, String mail, String address, String password1, String password2) throws ServiceError {

        AppUser client = userRepository.getById(id);

        validation(client.getName(), client.getDni(), phone, mail, password1, password2);

        client.setPhone(phone);
        client.setMail(mail); 
        client.setAddress(address);

        String encryptedPassword = new BCryptPasswordEncoder().encode(password1);
        client.setPassword(encryptedPassword);

        userRepository.save(client);
    }
    
    /*Find by Id*/
    @Transactional
    public Optional<AppUser> findAdminById(String id) {
        return userRepository.findById(id);
    }


    /*Validations*/
    private void validation(String name, String dni, String phone, String mail, String password1, String password2) throws ServiceError {

        Pattern pString = Pattern.compile("^([A-Za-z]+[ ]*){1,3}$");
        Pattern pNum = Pattern.compile("^[0-9]{8}$");
        Pattern pNumPhone = Pattern.compile("^[0-9]{8,10}$");
        Pattern pMail = Pattern.compile("\\b[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,4}\\b");

        Matcher mName = pString.matcher(name);
        Matcher mDni = pNum.matcher(dni);
        Matcher mPhone = pNumPhone.matcher(phone);
        Matcher mMail = pMail.matcher(mail);

        if (name == null || name.isEmpty()) {
            throw new ServiceError("Name not entered");
        } else if (!mName.matches()) {
            throw new ServiceError("The name must contain only letters.");
        }

        if (dni == null || dni.isEmpty()) {
            throw new ServiceError("Dni not entered.");
        } else if (!mDni.matches()) {
            throw new ServiceError("The DNI must contain 8 digits.");
        }

        if (phone == null || phone.isEmpty()) {
            throw new ServiceError("Phone not entered.");
        } else if (!mPhone.matches()) {
            throw new ServiceError("The phone must contain between 8 and 10 digits.");
        }

        if (password1 == null || password1.isEmpty()) {
            throw new ServiceError("Passwords not entered.");
        } else if (password1.equals(password2)) {
        } else {
            throw new ServiceError("The passwords are different.");
        }

        if (mail == null || mail.isEmpty()) {
            throw new ServiceError("Mail not entered.");
        } else if (!mMail.matches()) {
            throw new ServiceError("The mail is wrong.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {

        AppUser appUser = userRepository.findByMail(mail);

        if (appUser != null && appUser.getRol().equals("ROLE_ADMIN")) {
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            /*Lista de permisos*/

            GrantedAuthority auth = new SimpleGrantedAuthority("ROLE_ADMIN");
            authorities.add(auth);
            
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            
            User user = new User(appUser.getName(), appUser.getPassword(), authorities);
            
            session.setAttribute("usuario", appUser);
            return user;
            
        } else if(appUser != null && appUser.getRol().equals("ROLE_CLIENT")) {

            List<GrantedAuthority> authorities = new ArrayList<>();
            /*Lista de permisos*/

            GrantedAuthority auth = new SimpleGrantedAuthority("ROLE_CLIENT");
            authorities.add(auth);
            
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            
            User user = new User(appUser.getName(), appUser.getPassword(), authorities);

            session.setAttribute("usuario", appUser);
            return user;
            
        } else {
            return null;
        }      
    }

}
