package com.cg.controller;


import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.service.customer.ICustomerService;
import com.cg.service.deposit.IDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IDepositService depositService;


    @GetMapping
    public ModelAndView showListPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/list");

        List<Customer> customers = customerService.findAllByDeletedIsFalse();

        modelAndView.addObject("customers", customers);

        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView showSearchPage(@RequestParam String keySearch) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/list");

        keySearch = "%" + keySearch + "%";

        List<Customer> customers = customerService.findAllByFullNameLikeOrEmailLike(keySearch, keySearch);

        modelAndView.addObject("customers", customers);

        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView showCreatePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/create");

        modelAndView.addObject("customer", new Customer());

        return modelAndView;
    }

    @GetMapping("/update/{customerId}")
    public ModelAndView showCreatePage(@PathVariable Long customerId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/update");

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("customer", new Customer());
            modelAndView.addObject("error", "ID khách hàng không hợp lệ");
            return modelAndView;
        }

        modelAndView.addObject("customer", customerOptional.get());

        return modelAndView;
    }

    @GetMapping("/deposit/{cid}")
    public ModelAndView showDepositPage(@PathVariable Long cid) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/deposit");

        modelAndView.addObject("deposit", new Deposit());

        Optional<Customer> customerOptional = customerService.findById(cid);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("customer", new Customer());
            modelAndView.addObject("error", "ID khách hàng không hợp lệ");
            return modelAndView;
        }

        modelAndView.addObject("customer", customerOptional.get());

        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView create(@ModelAttribute Customer customer) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/create");

        try {
            customer.setId(0L);
            customer.setBalance(new BigDecimal(0L));
            customerService.save(customer);

            modelAndView.addObject("customer", new Customer());
        } catch (Exception e) {
            modelAndView.addObject("error", "Thao tác không thành công, vui lòng liên hệ Administrator");
        }

        return modelAndView;
    }

    @PutMapping("/update/{cid}")
    public ModelAndView update(@Validated @ModelAttribute Customer customer, @PathVariable Long cid, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/update");

        if (bindingResult.hasFieldErrors()) {
            modelAndView.addObject("errors", true);
            return modelAndView;
        }

        Optional<Customer> customerOptional = customerService.findById(cid);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("customer", new Customer());
            modelAndView.addObject("error", "ID khách hàng không hợp lệ");
            return modelAndView;
        }

        try {
            customer.setId(cid);
            customerService.save(customer);

            modelAndView.addObject("customer", customer);
        } catch (Exception e) {
            modelAndView.addObject("error", "Thao tác không thành công, vui lòng liên hệ Administrator");
        }

        return modelAndView;
    }

    @PostMapping("/deposit/{cid}")
    public ModelAndView deposit(@Validated @ModelAttribute Deposit deposit, @PathVariable Long cid, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/deposit");

        if (bindingResult.hasFieldErrors()) {
            modelAndView.addObject("errors", true);
            return modelAndView;
        }

        Optional<Customer> customerOptional = customerService.findById(cid);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("error", "ID khách hàng không hợp lệ");
            return modelAndView;
        }

        Customer customer = customerOptional.get();

        try {
            customerService.deposit(deposit, customer);

            modelAndView.addObject("deposit", new Deposit());
            modelAndView.addObject("customer", customer);
            modelAndView.addObject("success", "Gửi tiền thành công");
        } catch (Exception e) {
            modelAndView.addObject("deposit", new Deposit());
            modelAndView.addObject("customer", customer);
            modelAndView.addObject("error", "Thao tác không thành công, vui lòng liên hệ Administrator");
        }

        return modelAndView;
    }
}
