package org.bankmanagement.dataobject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionReportDto {
    private String withdrawUsername;
    private String depositUsername;
    private Long withdrawState;
    private Long transferSum;
}
