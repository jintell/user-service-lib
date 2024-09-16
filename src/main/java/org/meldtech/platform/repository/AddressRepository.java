package org.meldtech.platform.repository;

import org.meldtech.platform.domain.Address;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
public interface AddressRepository extends ReactiveCrudRepository<Address, Integer> {
}
