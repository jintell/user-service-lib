package org.meldtech.platform.model.dto.company.verifyMe;

public record Cac(String state,
                  String headOfficeAddress,
                  String city,
                  String status,
                  String companyEmail,
                  String rcNumber,
                  String classification,
                  String branchAddress,
                  String registrationDate,
                  String companyName,
                  String lga,
                  String companyType,
                  int affiliates,
                  int shareCapital,
                  String shareCapitalInWords
                  ) {}

