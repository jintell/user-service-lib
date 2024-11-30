package org.meldtech.platform.model.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.meldtech.platform.model.dto.company.verifyMe.Applicant;
import org.meldtech.platform.model.dto.company.verifyMe.Nin;
import org.meldtech.platform.model.dto.company.verifyMe.Status;
import org.meldtech.platform.model.dto.company.verifyMe.Summary;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NinRecord(int id, Applicant applicant, Summary summary, Status status, Nin nin) {}