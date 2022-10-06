package team.caltech.olmago.contract.domain.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static team.caltech.olmago.contract.domain.contract.QContract.contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, ContractRepositoryCustom {
  List<Contract> findByLastOrderId(long orderId);
}
