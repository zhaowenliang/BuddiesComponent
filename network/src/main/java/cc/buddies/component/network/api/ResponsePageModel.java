package cc.buddies.component.network.api;

/**
 * http分页请求响应基本结构
 *
 * @param <T> 数据体内数据对象类型
 */
public class ResponsePageModel<T> extends ResponseModel<T> {

    private int count; // 请求的数量
    private int start; // 请求的起始页码
    private int total; // 得到的数据总数

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
