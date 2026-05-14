package smCapstone.homecam.domain.pet.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetSpecies {
    CAT(15),   // YOLO COCO Dataset 기준 Cat 인덱스
    DOG(16),   // YOLO COCO Dataset 기준 Dog 인덱스
    ETC(-1);   // 기타 동물 (YOLO 미지원)

    private final int yoloClassId;
}
