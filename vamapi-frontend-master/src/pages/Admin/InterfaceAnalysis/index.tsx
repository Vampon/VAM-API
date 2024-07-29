import { PageContainer } from '@ant-design/pro-components';
import '@umijs/max';
import React, {useEffect, useState} from 'react';
import {Button, Card, Col, Divider, Form, Input, message, Row, Select, Space, Spin, Upload} from 'antd';
import ReactECharts from 'echarts-for-react';
import {listTopInvokeInterfaceInfoUsingGet,getLatestChartUsingGet,getDataStatisticsUsingGet} from "@/services/vamapi-backend/analysisController";

/**
 * 接口分析
 * @constructor
 */
const InterfaceAnalysis: React.FC = () => {

  const [data, setData] = useState<API.InterfaceInfoVO[]>([]);
  const [chartUser, setChartUser] = useState<API.BiResponse>();
  const [chartInterface, setChartInterface] = useState<API.BiResponse>();
  const [optionUser, setOptionUser] = useState<any>();
  const [optionInterface, setOptionInterface] = useState<any>();
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [pollingInterval, setPollingInterval] = useState<NodeJS.Timeout | null>(null);
  /**
   * 提交
   * @param values
   */
  const fetchData = async () => {
    try {
      const res = await listTopInvokeInterfaceInfoUsingGet();
      if (res.data) {
        setData(res.data);
      }
    } catch (error) {
      console.error('获取数据失败：', error);
    }
    try {
      await startPolling(); // 启动轮询
    } catch (error) {
      message.error('分析失败，' + error.message);
      setSubmitting(false);
    }
  };

  // 读取本地存储的数据
  useEffect(() => {
    const storedData = localStorage.getItem('data');
    const storedChartUser = localStorage.getItem('chartUser');
    const storedChartInterface = localStorage.getItem('chartInterface');
    const storedOptionUser = localStorage.getItem('optionUser');
    const storedOptionInterface = localStorage.getItem('optionInterface');

    if (storedData) setData(JSON.parse(storedData));
    if (storedChartUser) setChartUser(JSON.parse(storedChartUser));
    if (storedChartInterface) setChartInterface(JSON.parse(storedChartInterface));
    if (storedOptionUser) setOptionUser(JSON.parse(storedOptionUser));
    if (storedOptionInterface) setOptionInterface(JSON.parse(storedOptionInterface));

    fetchData(); // 初始化时获取数据
  }, []);

  // 保存数据到本地存储
  useEffect(() => {
    localStorage.setItem('data', JSON.stringify(data));
  }, [data]);

  useEffect(() => {
    localStorage.setItem('chartUser', JSON.stringify(chartUser));
  }, [chartUser]);

  useEffect(() => {
    localStorage.setItem('chartInterface', JSON.stringify(chartInterface));
  }, [chartInterface]);

  useEffect(() => {
    localStorage.setItem('optionUser', JSON.stringify(optionUser));
  }, [optionUser]);

  useEffect(() => {
    localStorage.setItem('optionInterface', JSON.stringify(optionInterface));
  }, [optionInterface]);


  const startPolling = async () => {
    if (pollingInterval) return; // 避免重复开启轮询
    setPollingInterval(setInterval(async () => {
      try {
        const res = await getLatestChartUsingGet();
        if (!Array.isArray(res.data) || res.data.length === 0) {
          message.error('没有获取到数据');
          return;
        }
        res.data.forEach(item => {
          if (!item) {
            message.error('分析失败');
            return;
          }
          if(item.name=="用户数据统计"){
            // message.success('用户数据分析成功');
            const chartOption = JSON.parse(item.genChart ?? '');
            if (!chartOption) {
              message.error('用户数据图表代码解析错误');
              return;
            }
            setChartUser(item);
            setOptionUser(chartOption);
          }else if(item.name=="接口调用数据统计"){
            // message.success('接口调用数据分析成功');
            const chartOption = JSON.parse(item.genChart ?? '');
            if (!chartOption) {
              message.error('接口调用数据图表代码解析错误');
              return;
            }
            setChartInterface(item);
            setOptionInterface(chartOption);
          }
        });
        setSubmitting(false);
      } catch (e: any) {
        message.error('分析失败，' + e.message);
        setSubmitting(false);
      }
    }, 30000)); // 每 30 秒轮询一次
  };
  const onFinish = async (values: any) => {
    if (submitting) {
      return;
    }
    setSubmitting(true);
    setChartInterface(undefined);
    setOptionInterface(undefined);
    setChartUser(undefined);
    setOptionUser(undefined);
    try {
      await getDataStatisticsUsingGet();
    } catch (e: any) {
      message.error('分析失败，' + e.message);
    }
  };

  // 映射：{ value: 1048, name: 'Search Engine' },
  const chartData = data.map(item => {
    return {
      value: item.totalNum,
      name: item.name,
    }
  })

  const option = {
    title: {
      text: '调用次数最多的接口TOP5',
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
    },
    legend: {
      orient: 'vertical',
      left: 'left',
    },
    series: [
      {
        name: 'Access From',
        type: 'pie',
        radius: '50%',
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
      },
    ],
  };

  return (
    <PageContainer>
      <ReactECharts option={option} />
      <Divider />
      <Row gutter={16}>
        <Col span={12}>
          <Card title="用户信息分析结论">
            {chartUser?.genResult ?? <div>请先在下方进行提交</div>}
            <Spin spinning={submitting}/>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="接口信息分析结论">
            {chartInterface?.genResult ?? <div>请先在下方进行提交</div>}
            <Spin spinning={submitting}/>
          </Card>
        </Col>
        <Divider />
        <Col span={12}>
          <Card title="用户信息可视化图表">
            {
              optionUser ? <ReactECharts option={optionUser} /> : <div>请先在下方进行提交</div>
            }
            <Spin spinning={submitting}/>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="接口信息可视化图表">
            {
              optionInterface ? <ReactECharts option={optionInterface} /> : <div>请先在下方进行提交</div>
            }
            <Spin spinning={submitting}/>
          </Card>
        </Col>
      </Row>
      <br/>
      <Form name="addChart" labelAlign="left" labelCol={{ span: 4 }}
            wrapperCol={{ span: 16 }} onFinish={onFinish} initialValues={{}}>
        <Form.Item
          wrapperCol={{ span: 16, offset: 4 }}
          style={{ display: 'flex', justifyContent: 'center' }}
        >
          <Space>
            <Button type="primary" htmlType="submit" loading={submitting} disabled={submitting} style={{ width: '150px' }}>
              开始分析
            </Button>
            <Button htmlType="reset" style={{ width: '150px' }}>重置信息</Button>
          </Space>
        </Form.Item>
      </Form>
    </PageContainer>
  );

};

export default InterfaceAnalysis;
