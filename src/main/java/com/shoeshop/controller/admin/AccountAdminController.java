package com.shoeshop.controller.admin;

import com.shoeshop.entity.Account;
import com.shoeshop.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/account")
public class AccountAdminController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("accounts", accountService.findAll());
        model.addAttribute("account", new Account());
        return "admin/account";
    }

    @GetMapping("/edit/{username}")
    public String edit(@PathVariable String username, Model model) {
        Account account = accountService.findById(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        model.addAttribute("accounts", accountService.findAll());
        model.addAttribute("account", account);

        return "admin/account";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Account account) {
        accountService.save(account);
        return "redirect:/admin/account/index";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Account account) {
        accountService.save(account);
        return "redirect:/admin/account/index";
    }

    @GetMapping("/delete/{username}")
    public String delete(@PathVariable String username) {
        accountService.deleteById(username);
        return "redirect:/admin/account/index";
    }
}