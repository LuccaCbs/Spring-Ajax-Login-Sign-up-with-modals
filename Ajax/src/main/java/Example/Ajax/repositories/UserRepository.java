
package Example.Ajax.repositories;

import Example.Ajax.entities.AppUser;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<AppUser, String>{
    
    @Query("SELECT c FROM AppUser c WHERE c.dni = :dni")
    public AppUser findByDni(@Param("dni")String dni);
    
    @Query("SELECT c FROM AppUser c WHERE c.mail = :mail")
    public AppUser findByMail(@Param("mail")String mail);
    
    @Query("SELECT c FROM AppUser c WHERE c.name = :name")
    public AppUser findByName(@Param("name")String name);
}
