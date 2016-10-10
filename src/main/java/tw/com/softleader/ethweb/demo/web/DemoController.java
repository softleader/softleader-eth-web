package tw.com.softleader.ethweb.demo.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import tw.com.softleader.commons.function.Unchecked;
import tw.com.softleader.util.StringUtils;

import tw.com.softleader.ethweb.demo.entity.Demo;
import tw.com.softleader.ethweb.demo.service.DemoService;

/**
 * @see https://github.com/softleader/softleader-framework-docs/wiki/QueryPrompt
 */
@Slf4j
@Controller
@RequestMapping("/demos")
public class DemoController {

  @Autowired
  private DemoService demoService;

  @ResponseBody
  @RequestMapping(method = {RequestMethod.POST})
  public Demo save(@RequestBody @Validated Demo entity,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      // FIXME: Validation failure SHOULD be handled.
      throw new IllegalArgumentException(bindingResult.getFieldErrors().stream()
          .map(f -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.joining(",")));
    }
    return demoService.save(entity);
  }

  @ResponseBody
  @RequestMapping(value = "/{id}", method = {RequestMethod.GET})
  public Demo getOne(@PathVariable("id") long id) {
    return demoService.getOne(id)
        // FIXME: Always return null if value is not present may inappropriate, or you can try like '.orElseThrow(() -> new NotFoundException(Demo.class, id))';
        .orElse(null);
  }

  @ResponseBody
  @RequestMapping(value = "/{ids}", method = {RequestMethod.DELETE, RequestMethod.POST})
  public void delete(@PathVariable("ids") List<Long> ids) {
    demoService.delete(ids);
  }
  
  @RequestMapping(method = RequestMethod.POST, value = "/upload")
  public String fileUpload(@RequestParam("files") List<MultipartFile> files) {

    if (!files.isEmpty()) {
      files.forEach(file -> {
        if (!file.isEmpty()) {
          try (BufferedReader buffer = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            log.info("filename: {}, content: {}", file.getOriginalFilename(), buffer.lines().collect(Collectors.joining("\n")));
          } catch (IOException | RuntimeException e) {
            throw Unchecked.rethrow(e);
          }
        } else {
          throw new IllegalArgumentException(StringUtils.format("Failued to upload [{}] because it was empty", file.getOriginalFilename()));
        }
      });
    } else {
      log.error("Failued to upload because files were empty");
    }

    return "redirect:/";
  }

}