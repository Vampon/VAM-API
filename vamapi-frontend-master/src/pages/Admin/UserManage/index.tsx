import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import { PageContainer, ProDescriptions, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Button, Drawer, message } from 'antd';
import React, { useRef, useState } from 'react';
import { SortOrder } from 'antd/lib/table/interface';
import {
  listUserUsingGet,//getUserByListUsingGet
  updateUserUsingPost,
} from '@/services/vamapi-backend/userController';
import UpdateModal from "@/pages/Admin/UserManage/components/UpdateModal";

const TableList: React.FC = () => {
  /**
   * @en-US The pop-up window of the distribution update window
   * @zh-CN 分布更新窗口的弹窗
   * */
  const [updateModalOpen, handleUpdateModalOpen] = useState<boolean>(false);
  const [showDetail, setShowDetail] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.UserVO>();
  const [reloadFlag, setReloadFlag] = useState(false);

  /**
   * 更新节点
   *
   * @param fields
   */
  const handleUpdate = async (fields: API.UserUpdateRequest) => {
    if (!currentRow) {
      return;
    }
    const hide = message.loading('修改中');
    try {
      await updateUserUsingPost({
        id: currentRow.id,
        ...fields,
      });
      hide();
      message.success('操作成功');
      return true;
    } catch (error: any) {
      hide();
      message.error('操作失败，' + error.message);
      return false;
    }
  };

  /**
   *  ban user
   * @zh-CN 禁用用户：将用户状态设置为 suspend，之后还可以解除封号
   *
   * @param record
   */
  const handleSuspend = async (fields: API.UserUpdateRequest) => {
    if (!currentRow) {
      return;
    }
    const hide = message.loading('操作中');
    try {
      if (currentRow.userRole === 'suspend') {
        currentRow.userRole = 'user';
      } else if (currentRow.userRole === 'user') {
        currentRow.userRole = 'suspend';
      }
      await updateUserUsingPost({
        id: currentRow.id,
        userRole: currentRow.userRole,
        ...fields,
      });
      hide();
      message.success('操作成功');
      // 点击按钮时更新状态变量的值
      setReloadFlag((prevFlag) => !prevFlag);
      return true;
    } catch (error: any) {
      hide();
      message.error('操作失败，' + error.message);
      return false;
    }
  };

  const columns: ProColumns<API.UserVO>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'indexBorder',
      width: 48,
    },
    {
      title: '用户名',
      dataIndex: 'userName',
      copyable: true,
      hideInSearch: true,
    },
    // {
    //   title: '账号',
    //   dataIndex: 'userAccount',
    //   copyable: true,
    //   hideInForm: true,
    //   hideInSearch: true,
    // },
    {
      title: '头像',
      dataIndex: 'userAvatar',
      valueType: 'image',
      hideInSearch: true,
      hideInForm: true,
      // render: (_, record) => (
      //   <div>
      //     <Image src={record.userAvatar} width={50}/>
      //   </div>
      // )
    },
    // {
    //   title: '性别',
    //   dataIndex: 'gender',
    //   valueEnum: {
    //     "0": { text: '男' },
    //     "1": { text: '女' },
    //   },
    // },
    {
      title: '角色',
      dataIndex: 'userRole',
      valueType: 'select',
      valueEnum: {
        user: { text: '普通用户', status: 'Default' },
        admin: { text: '管理员', status: 'Success' },
        suspend: { text: '已禁用', status: 'Error' },
      },
      hideInSearch: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <Button
          type="text"
          key="config"
          textHoverBg="#fff"
          onClick={() => {
            handleUpdateModalOpen(true);
            setCurrentRow(record);
          }}
        >
          修改
        </Button>,
        record.userRole === 'user' ? (
          <Button
            type="text"
            key="config"
            danger
            onClick={() => {
              setCurrentRow(record);
              handleSuspend(record);
            }}
          >
            禁用
          </Button>
        ) : null,
        record.userRole === 'suspend' ? (
          <Button
            type="text"
            key="config"
            danger
            onClick={() => {
              setCurrentRow(record);
              handleSuspend(record);
            }}
          >
            解除禁用
          </Button>
        ) : null,
      ],
    },
  ];
  return (
    <PageContainer>

      <ProTable<API.RuleListItem, API.PageParams>
        search={false}// 关闭全局搜索框
        headerTitle={'用户列表'}
        actionRef={actionRef}
        rowKey="key"
        // 使用reloadFlag作为key来触发组件重新加载
        key={reloadFlag.toString()}
        request={async (
          params,
          sort: Record<string, SortOrder>,
          filter: Record<string, (string | number)[] | null>,
        ) => {
          const res = await listUserUsingGet({
            ...params,
          });
          if (res?.data) {
            return {
              data: res?.data || [],
              success: true,
              total: res.data,
            };
          } else {
            return {
              data: [],
              success: false,
              total: 0,
            };
          }
        }}
        columns={columns}
      />
      <UpdateModal
        columns={columns}
        onSubmit={async (value) => {
          const success = await handleUpdate(value);
          if (success) {
            handleUpdateModalOpen(false);
            setCurrentRow(undefined);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        onCancel={() => {
          handleUpdateModalOpen(false);
          if (!showDetail) {
            setCurrentRow(undefined);
          }
        }}
        visible={updateModalOpen}
        values={currentRow || {}}
      />
      <Drawer
        width={600}
        open={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.userName && (
          <ProDescriptions<API.RuleListItem>
            column={2}
            title={currentRow?.userName}
            request={async () => ({
              data: currentRow || {},
            })}
            params={{
              id: currentRow?.userName,
            }}
            columns={columns as ProDescriptionsItemProps<API.RuleListItem>[]}
          />
        )}
      </Drawer>

    </PageContainer>
  );
};
export default TableList;
