import CodeHighlighting from '@/components/CodeHighlighting';
import { interfaceMethodList } from '@/enum/interfaceInfoEnum';
import { EditableProTable, ProForm } from '@ant-design/pro-components';
import '@umijs/max';
import { Button, Empty, Input, Select, Space, Spin } from 'antd';
import React, { useEffect } from 'react';

export type Props = {
  toolsInputPlaceholderValue: string;
  toolsInputValue: string;
  toolsInputDoubleClick: () => void;
  toolsInputChange: (e: any) => void;
  submitTools: () => void;
  toolsProEditTableDefaultData: any;
  toolsProEditTableData: any;
  setToolsProEditTableData: (data: any) => void;
  handleProEditTableAdd: (data: any) => void;
  requestParam: any;
  temporaryParams: any;
  toolsParamsColumns: any;
  data: any;
  toolsParams: any;
  toolsResult?: string;
  requestExampleActiveTabKey: string;
  toolsResultLoading: boolean;
};
const { Option } = Select;

const ToolsTab: React.FC<Props> = (props) => {
  const {
    toolsInputPlaceholderValue,
    toolsInputDoubleClick,
    toolsInputChange,
    submitTools,
    toolsProEditTableDefaultData,
    toolsProEditTableData,
    setToolsProEditTableData,
    handleProEditTableAdd,
    toolsParamsColumns,
    data,
    toolsResult,
    requestExampleActiveTabKey,
    toolsResultLoading,
  } = props;

  const selectBefore = (
    <Select defaultValue={data?.method} disabled={true}>
      <Option value={interfaceMethodList.GET.text}>GET</Option>
      <Option value={interfaceMethodList.POST.text}>POST</Option>
      <Option value={interfaceMethodList.PUT.text}>PUT</Option>
      <Option value={interfaceMethodList.DELETE.text}>DELETE</Option>
    </Select>
  );

  useEffect(() => {
    toolsInputDoubleClick();
  }, []);

  const parseResult = (result) => {
    try {
      const parsedResult = JSON.parse(result);
      if (parsedResult.code === 0 && parsedResult.data) {
        const innerData = JSON.parse(parsedResult.data);
        if (innerData.data && innerData.data.length > 0 && innerData.data[0].b64_image) {
          return { image: innerData.data[0].b64_image };
        }
      }
      return { text: result };
    } catch (e) {
      return { text: result };
    }
  };

  const result = toolsResult ? parseResult(toolsResult) : null;

  return (
    <>
      <ProForm style={{ width: '100%' }} submitter={false}>
        <ProForm.Group style={{ width: '100%' }}>
          <ProForm.Item label={<p className="highlightLine">请求地址</p>} style={{ width: '100%' }}>
            <Space.Compact style={{ width: '800px' }} onFocus={() => {}} onBlur={() => {}}>
              <Input
                size={'large'}
                addonBefore={selectBefore}
                placeholder={toolsInputPlaceholderValue}
                value={toolsInputPlaceholderValue}
                onDoubleClick={toolsInputDoubleClick}
                onChange={toolsInputChange}
                disabled={true}
              />
              <Button name={'send'} type="primary" size={'large'} onClick={submitTools}>
                发送
              </Button>
            </Space.Compact>
          </ProForm.Item>
          <ProForm.Item
            label={<p className="highlightLine">请求参数</p>}
            name="dataSource"
            trigger="onValuesChange"
          >
            <Button type={'default'} onClick={() => setToolsProEditTableData([])}>清空</Button>
            <br />
            <EditableProTable
              rowKey="id"
              toolBarRender={false}
              columns={toolsParamsColumns}
              defaultData={toolsProEditTableDefaultData}
              value={toolsProEditTableData}
              onChange={setToolsProEditTableData}
              loading={false}
              onValuesChange={setToolsProEditTableData}
              recordCreatorProps={{
                newRecordType: 'dataSource',
                position: 'bottom',
                onRecordAdd: handleProEditTableAdd,
                record: () => ({
                  id: (Math.random() * 1000000).toFixed(0),
                  addonBefore: 'ccccccc',
                  decs: 'testdesc',
                }),
              }}
              editable={{
                type: 'multiple',
              }}
            />
            <p className="highlightLine" style={{ marginTop: 25 }}>返回结果</p>
            <Spin spinning={toolsResultLoading}>
              {result ? (
                result.image ? (
                  <div>
                    <img src={`data:image/jpeg;base64,${result.image}`} alt="Decoded" style={{ maxWidth: '100%' }} />
                    <CodeHighlighting codeString={toolsResult} language={requestExampleActiveTabKey} />
                  </div>
                ) : (
                  <CodeHighlighting codeString={result.text} language={requestExampleActiveTabKey} />
                )
              ) : (
                <Empty description={"未发起调用，暂无请求信息"} />
              )}
            </Spin>
          </ProForm.Item>
        </ProForm.Group>
      </ProForm>
    </>
  );
};

export default ToolsTab;
