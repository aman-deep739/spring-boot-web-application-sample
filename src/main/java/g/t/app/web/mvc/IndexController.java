package g.t.app.web.mvc;

import g.t.app.config.security.UserDetails;
import g.t.app.domain.Note;
import g.t.app.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {

    private final NoteService noteService;

    @GetMapping({"/", ""})
    public String index(Model model, Pageable pageable) {
        model.addAttribute("greeting", "Hello Spring");

        model.addAttribute("notes", noteService.readAll(PageRequest.of(0, 20, Sort.by("createdDate").descending())));
        model.addAttribute("note", new Note());

        return "landing";
    }

    @GetMapping("/admin")
    public String adminHome(Model model, @AuthenticationPrincipal UserDetails principal) {
        model.addAttribute("message", getWelcomeMessage(principal));
        return "admin";
    }

    @GetMapping("/note")
    public String userHome(Model model, @AuthenticationPrincipal UserDetails principal) {
        model.addAttribute("message", getWelcomeMessage(principal));
        model.addAttribute("notes", noteService.readAllByUser(PageRequest.of(0, 20, Sort.by("createdDate").descending()), principal.getId()));
        model.addAttribute("note", new Note());
        return "note";
    }

    private String getWelcomeMessage(UserDetails principal) {
        return "Hello " + principal.getUsername() + "!";
    }

}
