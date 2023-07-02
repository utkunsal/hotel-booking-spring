package com.utk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/customers")
public class Main {
    private final CustomerRepository customerRepository;

    public Main(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping
    public List<Customer> getCustomers(){
        return customerRepository.findAll();
    }

    @PostMapping
    public void addCustomer(@RequestBody NewCustomerRequest request){
        Customer customer = new Customer();
        customer.setAge(request.age());
        customer.setName(request.name());
        customerRepository.save(customer);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer id){
        customerRepository.deleteById(id);
    }

    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer id,
                               @RequestBody NewCustomerRequest request){
        Customer customer = customerRepository.getReferenceById(id);
        if (request.name() != null) customer.setName(request.name());
        if (request.age() != null) customer.setAge(request.age());
        customerRepository.save(customer);
    }

    record NewCustomerRequest(
            String name,
            Integer age
    ){}


   /* @GetMapping("/greet")
    public GreetResponse hello(){
        GreetResponse response =  new GreetResponse(
                "hello",
                List.of("apple", "grapefruit", "peach", "chery"),
                new Person("Alex", "ALEX", 30)
                );
        return response;
    }

    record Person(String name, String lastName, int age){}
    record GreetResponse(
            String greet,
            List<String> fruits,
            Person person
    ){}*/
}
