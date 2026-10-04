package boy.wang.userservice.controller;

import boy.wang.userservice.common.Result;
import boy.wang.userservice.entity.Setmeal;
import boy.wang.userservice.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setmeal")
public class SetmealController {

    private final SetmealService setmealService;

    @Autowired
    public SetmealController(SetmealService setmealService) {
        this.setmealService = setmealService;
    }

    @GetMapping("/list")
    public Result<List<Setmeal>> list(@RequestParam(required = false) Long hospitalId,
                                      @RequestParam(required = false) String type) {
        List<Setmeal> list = setmealService.getSetmealList(hospitalId, type);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Setmeal> detail(@PathVariable Long id) {
        Setmeal setmeal = setmealService.getSetmealDetail(id);
        return Result.success(setmeal);
    }
}
