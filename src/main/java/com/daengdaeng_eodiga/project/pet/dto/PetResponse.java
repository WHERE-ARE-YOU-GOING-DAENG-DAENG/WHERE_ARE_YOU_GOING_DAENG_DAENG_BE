package com.daengdaeng_eodiga.project.pet.dto;

import java.io.Serializable;

public record PetResponse(int petId, String petName, String petImg) implements Serializable {
}
