package team.caltech.olmago.contract.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.caltech.olmago.contract.plm.DiscountType;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, ContractRepositoryCustom {
}
