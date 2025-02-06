package sandrakorpi.csnfribeloppapi.Models;

import sandrakorpi.csnfribeloppapi.Enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class User implements UserDetails {
    @Id // Endast en Id-annotering behövs
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester; // Koppling till Terminen

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id")) // Koppla roller till användare
    @Column(name = "role") // Kolumnnamn i tabellen user_roles
    private List<Role> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Om kontot aldrig ska löpa ut
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Om kontot aldrig ska låsas
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Om inloggningsuppgifter aldrig ska löpa ut
    }

    @Override
    public boolean isEnabled() {
        return true; // Om kontot alltid ska vara aktivt
    }

    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public String getUsername() {
        return userName;
    }
}
