package az.project.blogposter.service;

import az.project.blogposter.dto.request.PosterRequestDTO;
import az.project.blogposter.dto.response.PosterResponseDTO;
import az.project.blogposter.entity.Poster;
import az.project.blogposter.entity.User;
import az.project.blogposter.exception.GeneralException;
import az.project.blogposter.repository.PosterRepository;
import az.project.blogposter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PosterService {

    @Autowired
    private PosterRepository posterRepository;

    @Autowired
    private UserRepository userRepository;

    public PosterResponseDTO savePost(PosterRequestDTO posterRequestDTO) {
        User user = userRepository.findById(posterRequestDTO.getUserId())
                .orElseThrow(() -> new GeneralException("User not found with id: " + posterRequestDTO.getUserId()));

        Poster poster = new Poster();
        poster.setName(posterRequestDTO.getName());
        poster.setContent(posterRequestDTO.getContent());
        poster.setLikes(0);
        poster.setViews(0);
        poster.setCreation_date(new Date());
        poster.setPostedBy(user);

        Poster savedPoster = posterRepository.save(poster);
        return convertToResponseDTO(savedPoster);
    }

    public List<PosterResponseDTO> getAllPosters() {
        List<Poster> posters = posterRepository.findAll();
        return posters.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public PosterResponseDTO findPosterById(Integer postId) {
        Poster poster = posterRepository.findById(postId)
                .orElseThrow(() -> new GeneralException("Poster not found with id: " + postId));
        poster.setViews(poster.getViews() + 1);
        posterRepository.save(poster);
        return convertToResponseDTO(poster);
    }

    public void likePost(Integer postId) {
        Poster poster = posterRepository.findById(postId)
                .orElseThrow(() -> new GeneralException("Post not found with id: " + postId));
        poster.setLikes(poster.getLikes() + 1);
        posterRepository.save(poster);
    }

    public List<PosterResponseDTO> searchByName(String name) {
        List<Poster> posters = posterRepository.findAllByNameContaining(name);
        return posters.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Integer postId) {
        Poster poster = posterRepository.findById(postId)
                .orElseThrow(() -> new GeneralException("Poster not found with id: " + postId));
        posterRepository.deleteById(postId);
    }

    private PosterResponseDTO convertToResponseDTO(Poster poster) {
        PosterResponseDTO posterResponseDTO = new PosterResponseDTO();
        posterResponseDTO.setId(poster.getId());
        posterResponseDTO.setName(poster.getName());
        posterResponseDTO.setContent(poster.getContent());
        posterResponseDTO.setPostedBy(poster.getPostedBy().getUsername()); // Only the username
        posterResponseDTO.setCreation_date(poster.getCreation_date());
        posterResponseDTO.setLikes(poster.getLikes());
        posterResponseDTO.setViews(poster.getViews());
        return posterResponseDTO;
    }
}