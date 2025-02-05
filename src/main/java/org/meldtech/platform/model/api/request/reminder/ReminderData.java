package org.meldtech.platform.model.api.request.reminder;

import java.util.List;

public record ReminderData(ReminderApplicant applicant, List<String> remindedOn) {
}
