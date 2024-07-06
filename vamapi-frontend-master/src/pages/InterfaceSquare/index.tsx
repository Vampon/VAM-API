import React, {useEffect, useState} from "react";
import ProCard from "@ant-design/pro-card";
import {Badge, Card, Image, List, Spin, Tag} from "antd";
import Search from "antd/es/input/Search";
import {history} from "@umijs/max";
import {
  listInterfaceInfoByPageUsingGet, listInterfaceInfoBySearchTextPageUsingGet,
} from "@/services/vamapi-backend/interfaceInfoController";
import { StatisticCard } from '@ant-design/pro-components';
import RcResizeObserver from 'rc-resize-observer';
import {interfaceMethodList, interfaceStatusList} from "@/enum/interfaceInfoEnum";
import {
  EditOutlined,
  EllipsisOutlined,
  SettingOutlined,
} from '@ant-design/icons';
const InterfaceSquare: React.FC = () => {
  const [data, setData] = useState<API.InterfaceInfo[]>([]);
  const [searchText, setSearchText] = useState<string>('');
  const [total, setTotal] = useState<number>();
  const [pageSize] = useState<number>(12);
  const [totalInvokes, setTotalInvokes] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const [responsive, setResponsive] = useState(false);
  const loadData = async (current = 1) => {
    setLoading(true)
    const res = await listInterfaceInfoByPageUsingGet({
      current: current,
      name: searchText,
      pageSize: pageSize,
      sortField: 'totalInvokes',
      sortOrder: 'descend',
      description: searchText,
    });
    if (res.code === 0 && res.data) {
      const fetchedData = res?.data?.records || [];
      setData(fetchedData);
      // setData(res?.data?.records || []);
      setTotal(res.data.total)
      setLoading(false)
      const totalInvokesSum = fetchedData.reduce((sum, item) => sum + item.totalInvokes, 0);
      setTotalInvokes(totalInvokesSum);
    } else {
      setLoading(false)
    }
  };

  useEffect(() => {
    loadData();
  }, []);


  const imgStyle = {
    display: 'block',
    width: 42,
    height: 42,
  };


  const onSearch = async () => {
    const res = await listInterfaceInfoBySearchTextPageUsingGet({
      current: 1,
      searchText: searchText,
    });
    if (res.data) {
      setData(res?.data?.records || []);
      setTotal(res?.data?.total || 0)
    }
  };

  return (
    <>
      <RcResizeObserver
        key="resize-observer"
        onResize={(offset) => {
          setResponsive(offset.width < 596);
        }}
      >
        <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
          <StatisticCard
            statistic={{
              title: '接口调用',
              value: totalInvokes,
              tip:"test",
              icon: (
                <img
                  style={imgStyle}
                  src="https://gw.alipayobjects.com/mdn/rms_7bc6d8/afts/img/A*dr_0RKvVzVwAAAAAAAAAAABkARQnAQ"
                  alt="icon"
                />
              ),
            }}
          />
          <StatisticCard
            statistic={{
              title: '网站访问',
              value: 1754,
              tip:"test",
              icon: (
                <img
                  style={imgStyle}
                  src="https://gw.alipayobjects.com/mdn/rms_7bc6d8/afts/img/A*dr_0RKvVzVwAAAAAAAAAAABkARQnAQ"
                  alt="icon"
                />
              ),
            }}
          />
          <StatisticCard
            statistic={{
              title: '平均时延',
              value: 1754,
              icon: (
                <img
                  style={imgStyle}
                  src="https://gw.alipayobjects.com/mdn/rms_7bc6d8/afts/img/A*-jVKQJgA1UgAAAAAAAAAAABkARQnAQ"
                  alt="icon"
                />
              ),
            }}
          />
          <StatisticCard
            statistic={{
              title: '虚位待定',
              value: 1754,
              icon: (
                <img
                  style={imgStyle}
                  src="https://gw.alipayobjects.com/mdn/rms_7bc6d8/afts/img/A*FPlYQoTNlBEAAAAAAAAAAABkARQnAQ"
                  alt="icon"
                />
              ),
            }}
          />
        </StatisticCard.Group>
      </RcResizeObserver>
      <br/>
      {/*<Card hoverable style={{marginBlockStart: 8}}>*/}

      {/*</Card>*/}
      <ProCard layout="center" direction="column" type={"inner"}>
        <Search
          showCount
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value);
          }}
          allowClear
          size={"large"}
          maxLength={50}
          enterButton="搜索"
          placeholder={"没有找到心仪的接口？快搜索一下吧"}
          onSearch={onSearch}
          style={{maxWidth: 1000, height: 40}}/>
      </ProCard>
      <br/>
      <Spin spinning={loading}>
        <List
          pagination={{
            onChange: (page) => {
              loadData(page)
            },
            pageSize: pageSize,
            total: total
          }}
          grid={{
            gutter: 20,
            xs: 1,
            sm: 1,
            md: 2,
            lg: 4,
            xl: 5,
            xxl: 6
          }}
          dataSource={data}
          renderItem={(item, index) => (
            <List.Item>
              <ProCard key={index} bordered loading={loading} hoverable direction="column" style={{height: 270}} actions={[
                <Tag color="blue">{item.method}</Tag>,
                <Badge
                  status={interfaceStatusList[item.status.toString()].status}
                  text={interfaceStatusList[item.status.toString()].text}
                />
              ]} >
                <ProCard layout="center" onClick={() => {
                  history.push(`/interface_info/${item.id}`)
                }}>
                  <Badge count={item.totalInvokes} overflowCount={999999999} color='#eb4d4b'>
                    <Image style={{width: 80, borderRadius: 8, marginLeft: 10}}
                      // todo 如果接口没有图像，这时要设置一个默认图像，这里路径以后要改
                           src={item?.avatarUrl ?? "https://vamapi-1301005258.cos.ap-beijing.myqcloud.com/interface_avatar/1762855580239626241/API接口默认图像.png"}
                           fallback={"https://vamapi-1301005258.cos.ap-beijing.myqcloud.com/interface_avatar/1762855580239626241/API接口默认图像.png"}
                           alt={item.name}
                           preview={false}
                    />
                  </Badge>
                </ProCard>
                <ProCard onClick={() => {
                  history.push(`/interface_info/${item.id}`)
                }} layout="center" style={{marginTop: -10, fontSize: 16}}>
                  <span style={{fontWeight: 'bold', fontSize: 18}}>{item.name}</span>
                </ProCard>
                <ProCard onClick={() => {
                  history.push(`/interface_info/${item.id}`)
                }} layout="center" style={{marginTop: -18, fontSize: 14, textAlign: 'center'}}>
                  <span style={{fontSize: 14, textOverflow: 'ellipsis', overflow: 'hidden', width: '100%',whiteSpace: 'nowrap'}}>
                    {!item.description ? "暂无接口描述" : item.description.length > 15 ? item.description.slice(0, 15) + '...' : item.description}
                  </span>
                </ProCard>
              </ProCard>
            </List.Item>
          )}
        />
      </Spin>
    </>
  )
};

export default InterfaceSquare;
