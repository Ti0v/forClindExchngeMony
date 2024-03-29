package com.CachWeb.Cach.controller;

import com.CachWeb.Cach.entity.Currency;
import com.CachWeb.Cach.entity.ExchangeRate;
import com.CachWeb.Cach.entity.ExchangeRequest;
import com.CachWeb.Cach.entity.Image;
import com.CachWeb.Cach.repository.ImageRepository;
import com.CachWeb.Cach.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")



public class AdminController {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private ExchangeRequestService exchangeRequestService;

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping({"/list",""})

    public String listCurrencies(Model model, HttpServletRequest request, Principal principal) {
        List<Currency> currencies = currencyService.getAllCurrencies();
        model.addAttribute("currencies", currencies);

        // Check if the user is authenticated
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "admin/currency_list";
    }

    //To add Now Currency
    @GetMapping("/add")
    public String showAddCurrencyForm(Model model) {
        model.addAttribute("currency", new Currency());
        return "admin/add_currency_form";
    }
    @PostMapping("/add")
    public String addCurrency(@ModelAttribute Currency currency) {
        currencyService.addCurrency(currency);
        return "redirect:/admin/list";
    }
    @GetMapping("/delete/currency/{id}")
    public String deleteCurrency(@PathVariable Long id) {
        currencyService.remove(id);
        return "redirect:/admin/list";
    }

//exchange
    @GetMapping("/exchange-list")
    public String listExchange(Model model , HttpServletRequest request, Principal principal) {
     List<ExchangeRate> rateList = exchangeRateService.getAllExchangeRates();
        model.addAttribute("rateList", rateList);


        // Check if the user is authenticated
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);


        return "admin/exchange_list";
    }
    @GetMapping("/contacts")
    public String listcontact(Model model , HttpServletRequest request, Principal principal) {

        model.addAttribute("contact", contactService.getAllContacts());

        // Check if the user is authenticated
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "admin/contactreq";
    }
    @GetMapping("/delete/contact/{id}")
    public String deleteContact(@PathVariable Long id){
        contactService.remove(id);
       return  "redirect:/admin/contacts";
    }


    //To add Exchange
    @GetMapping("/addExchange")
    public String addExtchange(Model model){
        List<Currency> currencies = currencyService.getAllCurrencies();
        model.addAttribute("currencies",currencies);
        model.addAttribute("exchangrate", new ExchangeRate());
        return "admin/addExchange";
    }
    @PostMapping("/addExchange")
    public String saveExchange( @ModelAttribute ExchangeRate exchangeRate){
      exchangeRateService.save(exchangeRate);
        return "redirect:/admin/exchange-list";
    }
      @GetMapping("/delete/exchange/{id}")
          public String deleteExchange(@PathVariable Long id) {
        exchangeRateService.remove(id);
        return "redirect:/admin/exchange-list";
    }




    @GetMapping("/exchange-requests")
    public String showEntities(Model model) {
        List<ExchangeRequest> exchange = exchangeRequestService.getAllRequestsWithoutArchived();
        model.addAttribute("exchange", exchange);
        return "admin/excahngeRequests";
    }
    @PostMapping("/update-request")
    public String updateRequest(@RequestParam Long requestId,
                                @RequestParam("sendingAmount") BigDecimal sendingAmount,
                                @RequestParam("receivingAmount") BigDecimal receivingAmount,
                                @RequestParam("walletNumber") String walletNumber) {
// Logic to handle the update action
//        System.out.println("JRERE");
//        System.out.println(requestId);
//        System.out.println(walletNumber);
//        System.out.println(sendingAmount);
//        System.out.println(receivingAmount);

        exchangeRequestService.updateRequest(requestId, sendingAmount, receivingAmount, walletNumber);

        return "redirect:/admin/exchange-requests";
    }


    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Image image = imageRepository.getReferenceById(id);

        if (image != null && image.getData() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/exchange/delete/{id}")
    public String deleteRequests(@PathVariable("id") Long id) {
        exchangeRequestService.remove(id);
        return "redirect:/admin/all-requests-including-archived";
    }






    @GetMapping("/all-requests-including-archived")
    public String getAllRequestsWithArchived(Model model) {
        List<ExchangeRequest> allRequests = exchangeRequestService.getAllRequestsIncludingArchived();
        model.addAttribute("allRequests", allRequests);
        return "admin/all_requests_including_archived";
    }







    @PostMapping("/archive-request")
    public String archiveRequest(@RequestParam Long requestId) {
        exchangeRequestService.archiveRequest(requestId);
        return "redirect:/admin/all-requests-including-archived";
    }

}












