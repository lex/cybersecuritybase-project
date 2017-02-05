package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private JpaContext jpaContext;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address, Model model) {
        Signup s = signupRepository.save(new Signup(name, address, false));
        model.addAttribute("signup", s);

        return "done";
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public String details(@RequestParam(value = "id", defaultValue = "-1") String idString, Model model) {
        final long id = Long.parseLong(idString);
        final Signup s = signupRepository.findOne(id);
        model.addAttribute("signup", s);
        return "details";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public String cancel(@RequestParam String id) {
        final Signup s = signupRepository.findOne(Long.parseLong(id));
        signupRepository.delete(s);
        return "redirect:/";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestParam String name, Model model) {
        final EntityManager e = jpaContext.getEntityManagerByManagedType(Signup.class);
        Query q = e.createNativeQuery("select * from signup s where s.name like '" + name + "'", Signup.class);

        List<Signup> signups = q.getResultList();
        model.addAttribute("signups", signups);

        return "search";
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public String verify(@RequestParam(value = "id", defaultValue = "-1") String id, @RequestParam(value = "redirect", defaultValue = "/") String redirect) {
        final Signup s = signupRepository.findOne(Long.parseLong(id));
        s.setVerified(true);
        signupRepository.save(s);
        return "redirect:" + redirect;
    }
}
