package boy.wang.userservice.service;

import boy.wang.userservice.entity.Setmeal;

import java.util.List;

public interface SetmealService {

    List<Setmeal> getSetmealList(Long hospitalId, String type);

    Setmeal getSetmealDetail(Long id);
}
