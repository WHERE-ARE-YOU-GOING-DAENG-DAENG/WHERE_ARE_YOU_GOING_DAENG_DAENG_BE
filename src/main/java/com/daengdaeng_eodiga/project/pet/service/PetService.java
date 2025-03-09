package com.daengdaeng_eodiga.project.pet.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.daengdaeng_eodiga.project.global.exception.*;
import com.daengdaeng_eodiga.project.common.service.CommonCodeService;
import com.daengdaeng_eodiga.project.pet.dto.PetDetailResponseDto;
import com.daengdaeng_eodiga.project.pet.dto.PetListResponseDto;
import com.daengdaeng_eodiga.project.pet.dto.PetRegisterDto;
import com.daengdaeng_eodiga.project.pet.dto.PetUpdateDto;
import com.daengdaeng_eodiga.project.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.daengdaeng_eodiga.project.pet.entity.Pet;
import com.daengdaeng_eodiga.project.pet.repository.PetRepository;
import com.daengdaeng_eodiga.project.user.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {
	private final PetRepository petRepository;
	private final UserRepository userRepository;
	private final CommonCodeService commonCodeService;

	public List<Pet> fetchUserPets(User user) {
		return petRepository.findAllByUser(user);
	}
	public Pet findPet(int petId) {
		return petRepository.findById(petId).orElseThrow(PetNotFoundException::new);
	}
	public List<Pet> confirmUserPet(User user, Set<Integer> pets) {
		List<Pet> userPets = fetchUserPets(user);
		Map<Integer, Pet> userPetMap = userPets.stream()
			.collect(Collectors.toMap(Pet::getPetId, pet -> pet));

		List<Pet> confirmPets = pets.stream()
			.map(petId -> {
				Pet pet = userPetMap.get(petId);
				if (pet == null) {
					throw new PetNotFoundException();
				}
				return pet;
			})
			.toList();
		return confirmPets;
	}

    /**
     * 반려동물 등록
	 * 
	 * 반려동물의 이름,이미지,종,성별,크기,생년월일,중성화여부를 등록
     * 
     * @author 하진서
     * @return
     */	
	public void registerPet(int userId, PetRegisterDto requestDto) {
		User user = userRepository.findById(userId)
				.orElseThrow(UserNotFoundException::new);
		commonCodeService.isCommonCode(requestDto.getSpecies());
		commonCodeService.isCommonCode(requestDto.getGender());
		commonCodeService.isCommonCode(requestDto.getSize());
		Pet pet = Pet.builder()
				.name(requestDto.getName())
				.image(requestDto.getImage())
				.species(requestDto.getSpecies())
				.gender(requestDto.getGender())
				.size(requestDto.getSize())
				.birthday(parseDate(requestDto.getBirthday()))
				.neutering(requestDto.getNeutering())
				.user(user)
				.build();

		petRepository.save(pet);
	}

    /**
     * 반려동물 정보 수정
	 * 
	 * 반려동물의 이름,이미지,종,성별,크기,생년월일,중성화여부를 수정
     * 
     * @author 하진서
     * @return
     */		
	public void updatePet(int petId, PetUpdateDto updateDto) {
		Pet pet = petRepository.findById(petId)
				.orElseThrow(PetNotFoundException::new);
		commonCodeService.isCommonCode(updateDto.getSpecies());
		commonCodeService.isCommonCode(updateDto.getGender());
		commonCodeService.isCommonCode(updateDto.getSize());
		pet.setName(updateDto.getName());
		pet.setImage(updateDto.getImage());
		pet.setSpecies(updateDto.getSpecies());
		pet.setGender(updateDto.getGender());
		pet.setSize(updateDto.getSize());
		pet.setBirthday(parseDate(updateDto.getBirthday()));
		pet.setNeutering(updateDto.getNeutering());

		petRepository.save(pet);
	}

    /**
     * 반려동물 목록 조회.
	 * 
	 * 유저 페이지에 표시할 반려동물의 목록 조회
     * 
     * @author 하진서
     * @return List<PetListResponseDto>
     */   
	public List<PetListResponseDto> fetchUserPetListDto(int userId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		List<Pet> pets = petRepository.findAllByUser(user);

		return pets.stream()
				.map(pet -> PetListResponseDto.builder()
						.petId(pet.getPetId())
						.name(pet.getName())
						.image(pet.getImage())
						.species(commonCodeService.getCommonCodeName(pet.getSpecies()))
						.gender(commonCodeService.getCommonCodeName(pet.getGender()))
						.size(commonCodeService.getCommonCodeName(pet.getSize()))
						.build())
				.collect(Collectors.toList());
	}

    /**
     * 반려동물 상세정보 조회.
	 * 
	 * 유저 페이지에 표시할 반려동물의 상세정보 조회.
	 * 반려동물의 ID,이름,이미지,종,성별,크기,생년월일,중성화여부가 포함
     * 
     * @author 하진서
     * @return PetDetailResponseDto
     */  	
	public PetDetailResponseDto fetchPetDetail(int petId) {
		Pet pet = petRepository.findById(petId)
				.orElseThrow(PetNotFoundException::new);

		String speciesName = commonCodeService.getCommonCodeName(pet.getSpecies());
		String genderName = commonCodeService.getCommonCodeName(pet.getGender());
		String sizeName = commonCodeService.getCommonCodeName(pet.getSize());

		return PetDetailResponseDto.builder()
				.petId(pet.getPetId())
				.name(pet.getName())
				.image(pet.getImage())
				.species(speciesName)
				.gender(genderName)
				.birthday(new SimpleDateFormat("yyyy-MM-dd").format(pet.getBirthday()))
				.neutering(pet.getNeutering())
				.size(sizeName)
				.build();
	}

    /**
     * 반려동물 정보 삭제.
	 * 
	 * 반려동물 DB에서 해당 반려동물 삭제
     * 
     * @author 하진서
     * @return
     */	
	public void deletePet(int userId, int petId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		Pet pet = petRepository.findById(petId).orElseThrow(PetNotFoundException::new);
		if (!pet.getUser().equals(user)) { throw new UserUnauthorizedException();}

		petRepository.delete(pet);
	}

	/**
	 * 날짜 타입 변환.
	 * 
	 * String 타입의 날짜를 Date 타입으로 변경
	 * 
	 * @author 하진서
	 * @return Date
	 */
	private Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			throw new DateNotFoundException();
		}
	}
}