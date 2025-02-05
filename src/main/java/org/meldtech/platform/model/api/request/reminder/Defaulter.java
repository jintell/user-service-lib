package org.meldtech.platform.model.api.request.reminder;

import lombok.Builder;

import java.util.List;

@Builder
public record Defaulter(String name, List<String> reminders, String to) {
}
