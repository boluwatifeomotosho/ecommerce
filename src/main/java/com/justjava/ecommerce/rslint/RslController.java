package com.justjava.ecommerce.rslint;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rslint")
public class RslController {

    @GetMapping({"", "/"})
    public String home(Model model) {
        model.addAttribute("activePage", "home");
        return "rslint/home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("activePage", "about");
        return "rslint/about";
    }

    @GetMapping("/mission")
    public String mission(Model model) {
        model.addAttribute("activePage", "about");
        return "rslint/mission";
    }

    @GetMapping("/vision")
    public String vision(Model model) {
        model.addAttribute("activePage", "about");
        return "rslint/vision";
    }

    @GetMapping("/philosophy")
    public String philosophy(Model model) {
        model.addAttribute("activePage", "about");
        return "rslint/philosophy";
    }

    @GetMapping("/brands")
    public String brands(Model model) {
        model.addAttribute("activePage", "brands");
        return "rslint/brands";
    }

    @GetMapping("/partners")
    public String partners(Model model) {
        model.addAttribute("activePage", "partners");
        return "rslint/partners";
    }

    @GetMapping("/distributors")
    public String distributors(Model model) {
        model.addAttribute("activePage", "distributors");
        return "rslint/distributors";
    }

    @PostMapping("/distributors")
    public String submitDistributor(@RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam(required = false) String phone,
                                    @RequestParam(required = false) String company,
                                    @RequestParam(required = false) String address,
                                    @RequestParam(required = false) String interest,
                                    RedirectAttributes ra) {
        ra.addFlashAttribute("submitted", true);
        ra.addFlashAttribute("submittedName", name);
        return "redirect:/rslint/distributors";
    }

    @GetMapping("/awards")
    public String awards(Model model) {
        model.addAttribute("activePage", "about");
        return "rslint/awards";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("activePage", "media");
        return "rslint/gallery";
    }

    @GetMapping("/video")
    public String video(Model model) {
        model.addAttribute("activePage", "media");
        return "rslint/video";
    }

    @GetMapping("/blog")
    public String blog(Model model) {
        model.addAttribute("activePage", "media");
        return "rslint/blog";
    }

    @GetMapping("/blog/rsl-recognition-award")
    public String blogRecognitionAward(Model model) {
        model.addAttribute("activePage", "media");
        return "rslint/blog-recognition-award";
    }

    @GetMapping("/blog/peak-vegetable-juice-recipe")
    public String blogPeakRecipe(Model model) {
        model.addAttribute("activePage", "media");
        return "rslint/blog-peak-recipe";
    }

    @GetMapping("/blog/nestle-chefs-day")
    public String blogChefsDay(Model model) {
        model.addAttribute("activePage", "media");
        return "rslint/blog-chefs-day";
    }

    @GetMapping("/blog/nestle-choose-wellness-campaign")
    public String blogWellnessCampaign(Model model) {
        model.addAttribute("activePage", "media");
        return "rslint/blog-wellness-campaign";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("activePage", "contact");
        return "rslint/contact";
    }

    @PostMapping("/contact")
    public String submitContact(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam(required = false) String subject,
                                @RequestParam String message,
                                RedirectAttributes ra) {
        ra.addFlashAttribute("submitted", true);
        ra.addFlashAttribute("submittedName", name);
        return "redirect:/rslint/contact";
    }
}
