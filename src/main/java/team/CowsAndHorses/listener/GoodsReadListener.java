package team.CowsAndHorses.listener;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import jakarta.servlet.annotation.WebListener;
import team.CowsAndHorses.dao.GoodsDao;
import team.CowsAndHorses.domain.Goods;

public class GoodsReadListener implements ReadListener<Goods> {

    private GoodsDao goodsDao;

    public GoodsReadListener(GoodsDao goodsDao){
        this.goodsDao = goodsDao;
    }


    @Override
    public void invoke(Goods goods, AnalysisContext analysisContext) {
        System.out.println("读取到：" + goods);
        goodsDao.insert(goods);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("读取完毕！！！");
    }
}