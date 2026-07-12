package com.example.adoption;

import com.example.adoption.domain.PetStatus;
import com.example.adoption.model.Pet;
import com.example.adoption.model.PetPicture;
import com.example.adoption.repository.PetPictureRepository;
import com.example.adoption.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PetPictureRepositoryTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetPictureRepository petPictureRepository;

    @Test
    void shouldPersistPetWithPicturesAndFindPictureById() {
        Pet pet = new Pet();
        pet.setName("Luna");
        pet.setSpecies("Dog");
        pet.setStatus(PetStatus.AVAILABLE);

        PetPicture picture = new PetPicture();
        picture.setData(new byte[]{0x1, 0x2, 0x3});
        picture.setContentType("image/png");
        pet.addPicture(picture);

        Pet saved = petRepository.save(pet);
        assertThat(saved.getPictures()).hasSize(1);

        Long pictureId = saved.getPictures().get(0).getId();
        Optional<PetPicture> loadedPicture = petPictureRepository.findById(pictureId);

        assertThat(loadedPicture).isPresent();
        assertThat(loadedPicture.get().getData()).containsExactly((byte)0x1, (byte)0x2, (byte)0x3);
        assertThat(loadedPicture.get().getContentType()).isEqualTo("image/png");
        assertThat(loadedPicture.get().getPet().getId()).isEqualTo(saved.getId());
    }
}
