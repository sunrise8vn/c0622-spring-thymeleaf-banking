package com.cg.service.customer;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.service.IGeneralService;

import java.util.List;

public interface ICustomerService extends IGeneralService<Customer> {

    List<Customer> findAllByFullNameLikeOrEmailLike(String fullName, String email);

    List<Customer> findAllByDeletedIsFalse();

    void deposit(Deposit deposit, Customer customer);
}
