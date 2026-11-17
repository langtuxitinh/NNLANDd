package com.javaweb.controller.web;

import com.javaweb.enums.DistrictCode;
import com.javaweb.enums.ProvinceCode;
import com.javaweb.enums.TypeCode;
import com.javaweb.model.dto.BuildingDTO;
import com.javaweb.model.response.BuildingCardResponse;
import com.javaweb.service.IBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import com.javaweb.model.request.BuildingSearchRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller(value = "homeControllerOfWeb")
public class HomeController {

	@Autowired
	private IBuildingService buildingService;

	/**
	 * Trang chủ + tìm kiếm thật theo tỉnh/thành, quận/huyện, loại bất động sản.
	 * Giá trị người dùng gửi lên được kiểm tra với danh mục có sẵn, giá trị lạ bị bỏ qua.
	 */
	@RequestMapping(value = "/trang-chu", method = RequestMethod.GET)
	public ModelAndView homePage(@RequestParam(value = "province", required = false) String province,
								 @RequestParam(value = "district", required = false) String district,
								 @RequestParam(value = "typeCode", required = false) String typeCode) {
		ModelAndView mav = new ModelAndView("web/home");

		String selectedDistrict = DistrictCode.fromCode(district) != null ? district : "";
		String selectedProvince = ProvinceCode.getProvinces().containsKey(province) ? province : "";
		String selectedType = TypeCode.getRentType().containsKey(typeCode) ? typeCode : "";
		// Quận quyết định tỉnh/thành, tránh trường hợp chọn lệch (vd: Hà Nội + Quận 1)
		if (!selectedDistrict.isEmpty()) {
			selectedProvince = DistrictCode.fromCode(selectedDistrict).getProvince().name();
		}

		// Dữ liệu cho 3 ô chọn
		Map<String, Map<String, String>> districtsByProvince = new LinkedHashMap<>();
		for (String provinceCode : ProvinceCode.getProvinces().keySet()) {
			districtsByProvince.put(provinceCode, DistrictCode.getDistrictByProvince(provinceCode));
		}
		mav.addObject("provinces", ProvinceCode.getProvinces());
		mav.addObject("districtsByProvince", districtsByProvince);
		mav.addObject("typeCodes", TypeCode.getRentType());
		mav.addObject("selectedProvince", selectedProvince);
		mav.addObject("selectedDistrict", selectedDistrict);
		mav.addObject("selectedType", selectedType);

		boolean searched = !selectedProvince.isEmpty() || !selectedDistrict.isEmpty() || !selectedType.isEmpty();
		mav.addObject("searched", searched);

		if (searched) {
			List<BuildingCardResponse> results = buildingService.searchBuildingCards(selectedProvince, selectedDistrict, selectedType);
			mav.addObject("resultBuildings", results);
			mav.addObject("resultCount", results.size());

			List<String> summary = new ArrayList<>();
			if (!selectedDistrict.isEmpty()) {
				summary.add(DistrictCode.fromCode(selectedDistrict).getDistrictName() + ", "
						+ ProvinceCode.valueOf(selectedProvince).getProvinceName());
			} else if (!selectedProvince.isEmpty()) {
				summary.add(ProvinceCode.valueOf(selectedProvince).getProvinceName());
			}
			if (!selectedType.isEmpty()) {
				summary.add("loại " + TypeCode.getRentType().get(selectedType));
			}
			mav.addObject("searchSummary", String.join(" - ", summary));
		} else {
			List<BuildingCardResponse> all = buildingService.searchBuildingCards("", "", "");

			// Nổi bật: tối đa 6 tòa có giá thuê cao nhất
			List<BuildingCardResponse> featured = new ArrayList<>(all);
			featured.sort(Comparator.comparing(BuildingCardResponse::getRentPrice,
					Comparator.nullsLast(Comparator.<Integer>reverseOrder())));
			mav.addObject("featuredBuildings", featured.subList(0, Math.min(6, featured.size())));

			// Mới nhất: 4 tòa vừa thêm (danh sách đã sắp theo id giảm dần)
			mav.addObject("latestBuildings", all.subList(0, Math.min(4, all.size())));
		}
		return mav;
	}

    @GetMapping(value="/gioi-thieu")
    public ModelAndView introducceBuiding(){
        ModelAndView mav = new ModelAndView("web/introduce");
        return mav;
    }

    @GetMapping(value="/san-pham")
    public ModelAndView buidingList(){
        ModelAndView mav = new ModelAndView("/web/list");
        return mav;
    }

    @GetMapping(value="/tin-tuc")
    public ModelAndView news(){
        ModelAndView mav = new ModelAndView("/web/news");
        return mav;
    }

    @GetMapping(value="/lien-he")
    public ModelAndView contact(){
        ModelAndView mav = new ModelAndView("/web/contact");
        return mav;
    }

	/** Trang chi tiet toa nha lay du lieu that tu database: /chi-tiet-1, /chi-tiet-2, ... */
	@GetMapping(value = "/chi-tiet-{id}")
	public ModelAndView buildingDetail(@PathVariable("id") Long id) {
		BuildingDTO building = buildingService.findBuildingById(id);
		if (building == null || building.getId() == null) {
			return new ModelAndView("redirect:/san-pham");
		}
		ModelAndView mav = new ModelAndView("web/detail");
		mav.addObject("building", building);
		mav.addObject("districtName", DistrictCode.getDistrict().get(building.getDistrict()));
		mav.addObject("typeName", TypeCode.getRentType().get(building.getTypeCode()));
		return mav;
	}

	/**
	 * Cac nut "Xem chi tiet" trong giao dien mau dang tro toi ./ChiTiet.html.
	 * Giu duong dan do va hien thi toa nha theo ?id= (mac dinh la toa nha so 1).
	 */
	@GetMapping(value = "/ChiTiet.html")
	public ModelAndView legacyDetail(@RequestParam(value = "id", defaultValue = "1") Long id) {
		return buildingDetail(id);
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView mav = new ModelAndView("login");
		return mav;
	}

	@RequestMapping(value = "/access-denied", method = RequestMethod.GET)
	public ModelAndView accessDenied() {
		return new ModelAndView("redirect:/login?accessDenied");
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return new ModelAndView("redirect:/trang-chu");
	}
}