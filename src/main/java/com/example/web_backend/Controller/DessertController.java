package com.example.web_backend.Controller;

import com.example.web_backend.config.StateConstant;
import com.example.web_backend.entity.*;
import com.example.web_backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
public class DessertController {
    @Autowired
    private DessertMapper dessertMapper;
    @Autowired
    private DessertOrderMapper dessertOrderMapper;
    @Autowired
    private UserMapper userMapper;
    private final String SourcePath = this.getClass().getClassLoader().getResource("static/").getPath();
    private final String dessertImagePath = SourcePath+"dessert_pic/";
    @GetMapping("/admin/getAllDessert")
    public MessageEntity<List<Dessert>> getAllDessert() {
        List<Dessert> desserts = dessertMapper.selectList(null);
        for (Dessert dessert:desserts) {
            String imagePath = dessert.getImagePath();
            try {
                File imageFile = new File(imagePath);
                byte[] imageData = Files.readAllBytes(imageFile.toPath());
                dessert.setImageResource(imageData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return MessageEntity.success(desserts);
    }
    @GetMapping("/admin/getDessertByName")
    public MessageEntity<List<Dessert> > getDessertByName(@RequestParam String name) {
        List<Dessert> desserts = dessertMapper.selectByName(name);
        for (Dessert dessert :desserts) {
            String imagePath = dessertImagePath+dessert.getImagePath();
            try {
                File imageFile = new File(imagePath);
                byte[] imageData = Files.readAllBytes(imageFile.toPath());
                dessert.setImageResource(imageData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return MessageEntity.success(desserts);
    }

    @PostMapping("/admin/addNewDessert")
    public MessageEntity<String> addNewDessert(@RequestBody Dessert dessert, @RequestParam("file") MultipartFile file) {
        if (dessertMapper.selectByName(dessert.getName()) != null)
            return MessageEntity.error(StateConstant.DESSERT_NOT_FOUND_CODE,StateConstant.DESSERT_NOT_FOUND_MSG);
        if (file.isEmpty())
            return MessageEntity.error(StateConstant.IMAGE_IS_NULL_CODE,StateConstant.IMAGE_IS_NULL_MSG);

        String filePath = dessertImagePath + file.getOriginalFilename();
        dessert.setImagePath(filePath);
        try {
            File destFile = new File(filePath);
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dessertMapper.insert(dessert);
        return MessageEntity.success(StateConstant.HTTP_OK_MSG);//"添加成功";
    }

    @PostMapping("/admin/addDessert")
    public MessageEntity<String> addDessert(@RequestParam int id, @RequestParam int nums) {
        Dessert dessert = dessertMapper.selectById(id);
        if (dessert == null)
            return MessageEntity.error(StateConstant.DESSERT_NOT_FOUND_CODE,StateConstant.DESSERT_NOT_FOUND_MSG);
        dessertMapper.updateStorage(dessert.getStorage() + nums, id);
        return MessageEntity.success(StateConstant.HTTP_OK_MSG);//"添加成功";
    }

    @PostMapping("/admin/deleteDessert")
    public MessageEntity<String> deleteDessert(@RequestParam int id) {
        Dessert dessert = dessertMapper.selectById(id);
        if (dessert == null)
            return  MessageEntity.error(StateConstant.DESSERT_NOT_FOUND_CODE,StateConstant.DESSERT_NOT_FOUND_MSG);
        dessertMapper.deleteById(dessert.getId());
        return MessageEntity.success(StateConstant.HTTP_OK_MSG);//"下架成功";
    }



    @GetMapping("/admin/getUser")
    public User getUser(@RequestParam String name){
        return userMapper.selectByUsername(name);
    }
}
