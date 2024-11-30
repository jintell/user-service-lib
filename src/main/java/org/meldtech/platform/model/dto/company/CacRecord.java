package org.meldtech.platform.model.dto.company;

import org.meldtech.platform.model.dto.company.verifyMe.Cac;
import org.meldtech.platform.model.dto.company.verifyMe.Metadata;
import org.meldtech.platform.model.dto.company.verifyMe.Status;
import org.meldtech.platform.model.dto.company.verifyMe.Summary;

public record CacRecord(int id,
                        Metadata metadata,
                        Summary summary,
                        Status status,
                        Cac cac) {
}


