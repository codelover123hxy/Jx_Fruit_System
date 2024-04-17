package team.CowsAndHorses.controller;

import cn.hutool.system.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team.CowsAndHorses.constant.ErrorCode;
import team.CowsAndHorses.domain.Address;
import team.CowsAndHorses.dto.AjaxResult;
import team.CowsAndHorses.service.AddressService;
import team.CowsAndHorses.util.JwtUtil;

import java.util.List;
import java.util.Map;

import static team.CowsAndHorses.util.ParseUtil.parseToken;

/**
 * @author LittleHorse
 * @version 1.0
 */


@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/api/address")
public class AddressController {
    final AddressService addressService;
    @PostMapping("/add")
    @ResponseBody
    public Object addAddress(HttpServletRequest request, @RequestBody Address addressInfo){
        Integer userId = parseToken(request);
        addressInfo.setUserId(userId);
        addressService.addAddress(addressInfo);
        return AjaxResult.SUCCESS();
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Object deleteAddress(@PathVariable Integer id){
        addressService.removeAddress(id);
        return AjaxResult.SUCCESS();
    }

    @PostMapping("/update")
    @ResponseBody
    public Object updateAddress(@RequestBody Address newInfo) {
        addressService.updateAddress(newInfo);
        return AjaxResult.SUCCESS();
    }

    @GetMapping("/query/info")
    @ResponseBody
    public Object getAddressInfo(HttpServletRequest request) {
        Integer userId = parseToken(request);
        List<Address> addressList = addressService.getAddressList(userId);
        return AjaxResult.SUCCESS(addressList);
    }

    @GetMapping("/default")
    @ResponseBody
    public Object getDefaultAddress(HttpServletRequest request) {
        Integer userId = parseToken(request);
        Address defaultAddress = addressService.getDefaultAddress(userId);
        return AjaxResult.SUCCESS(defaultAddress);
    }

    @GetMapping("/query/{id}")
    @ResponseBody
    public Object getAddressById(@PathVariable Integer id){
        Address address = addressService.getAddressById(id);
        return AjaxResult.SUCCESS(address);
    }

    @GetMapping("/query/all")
    @ResponseBody
    public Object getAllAddress(){
        List<Address> addressList = addressService.getAllAddress();
        return AjaxResult.SUCCESS(addressList);
    }
}