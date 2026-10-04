package boy.wang.userservice.service.impl;

import boy.wang.userservice.common.ResultCode;
import boy.wang.userservice.entity.Setmeal;
import boy.wang.userservice.exception.BusinessException;
import boy.wang.userservice.mapper.SetmealMapper;
import boy.wang.userservice.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    private final SetmealMapper setmealMapper;

    @Autowired
    public SetmealServiceImpl(SetmealMapper setmealMapper) {
        this.setmealMapper = setmealMapper;
    }

    @Override
    public List<Setmeal> getSetmealList(Long hospitalId, String type) {
        return setmealMapper.selectByCondition(hospitalId, type);
    }

    @Override
    public Setmeal getSetmealDetail(Long id) {
        Setmeal setmeal = setmealMapper.selectByIdWithCheckitems(id);
        if (setmeal == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "套餐不存在");
        }
        return setmeal;
    }
}
