package org.meldtech.platform.model.api.request.reminder;

import java.util.List;

public record ReminderInfo(String type, ReminderData data, List<ReminderData> reminders) {
}
