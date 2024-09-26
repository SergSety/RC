package by.nca.rc.controllers;

import by.nca.rc.models.payload.CreateChangeRemarkRequest;
import by.nca.rc.models.payload.RemarkResponse;
import by.nca.rc.services.RemarkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static by.nca.rc.util.ErrorsResponse.errorsResponse;

@RestController
@RequestMapping
@CrossOrigin(origins="*")
public class RemarkController {

    private final RemarkService remarkService;

    public RemarkController(RemarkService remarkService) {

        this.remarkService = remarkService;
    }

    @PostMapping("/news/{id}/remark")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUBSCRIBER')")
    public ResponseEntity<HttpStatus> createRemark(@PathVariable("id") Long id, // It's news id
                                                     @RequestBody
                                                     @Valid CreateChangeRemarkRequest createChangeRemarkRequest,
                                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) errorsResponse(bindingResult);

        remarkService.createRemark(id, createChangeRemarkRequest);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // We can add sorting by others parameters + direction (The user must install)

    @GetMapping("/news/{id}/remark")
    public ResponseEntity<RemarkResponse> findAllRemarkByNewsId(@PathVariable("id") Long id,
                                                                  @RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "3") Integer size) {

        return new ResponseEntity<>(remarkService.findAllRemarkByNewsId(id, page, size), HttpStatus.OK);
    }

    @PutMapping("/remark/{id}")
    @PreAuthorize("hasRole('ADMIN') or @methodAccess.hasCustomerIdForRemark(#id)")
    public ResponseEntity<HttpStatus> changeRemark(@PathVariable("id") Long id,
                                                 @RequestBody
                                                 @Valid CreateChangeRemarkRequest createChangeRemarkRequest,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) errorsResponse(bindingResult);

        remarkService.changeRemark(createChangeRemarkRequest, id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping({"/remark/{id}"})
    @PreAuthorize("hasRole('ADMIN') or @methodAccess.hasCustomerIdForRemark(#id)")
    public ResponseEntity<HttpStatus> deleteRemark(@PathVariable("id") Long id) {
        remarkService.deleteRemark(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
